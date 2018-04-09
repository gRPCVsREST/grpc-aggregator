package com.grpcvsrest.grpc.aggregator;

import brave.Tracing;
import brave.grpc.GrpcTracing;
import com.grpcvsrest.grpc.ContentStreamingServiceGrpc;
import com.grpcvsrest.grpc.ResponseType;
import com.grpcvsrest.grpc.aggregator.AggregationStreamingService.TypedStub;
import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.NettyServerBuilder;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.urlconnection.URLConnectionSender;

import java.io.IOException;

import static brave.sampler.Sampler.ALWAYS_SAMPLE;

/**
 * Starts gRPC server with {@link AggregationStreamingService} and {@link ResponseTypeService}.
 */
public class AggregationServer {

    public static void main(String[] args) throws IOException, InterruptedException {

        GrpcTracing grpcTracing = grpcTracing();

        String contentAHost = System.getenv("content_a_host");
        int contentAPort = Integer.valueOf(System.getenv("content_a_port"));

        String conentBHost = System.getenv("content_b_host");
        int contentBPort = Integer.valueOf(System.getenv("content_b_port"));

        ManagedChannel serviceAChannel = NettyChannelBuilder.forAddress(contentAHost, contentAPort)
                .intercept(grpcTracing.newClientInterceptor())
                .usePlaintext()
                .build();
        ManagedChannel serviceBChannel = NettyChannelBuilder.forAddress(conentBHost, contentBPort)
                .intercept(grpcTracing.newClientInterceptor())
                .usePlaintext()
                .build();

        TypedStub stubA = new TypedStub(ContentStreamingServiceGrpc.newStub(serviceAChannel),
                ResponseType.forNumber(1));
        TypedStub stubB = new TypedStub(ContentStreamingServiceGrpc.newStub(serviceBChannel),
                ResponseType.forNumber(2));

        AggregationIdRepository idRepository = new AggregationIdRepository();
        Server grpcServer = NettyServerBuilder.forPort(8080)
                .addService(new AggregationStreamingService(idRepository, stubA, stubB))
                .addService(new ResponseTypeService(idRepository))
                .intercept(grpcTracing.newServerInterceptor()).build()
                .start();

        Runtime.getRuntime().addShutdownHook(new Thread(grpcServer::shutdown));
        grpcServer.awaitTermination();
    }

    private static GrpcTracing grpcTracing() {

        String zipkinHost = System.getenv("ZIPKIN_SERVICE_HOST");
        int zipkinPort = Integer.valueOf(System.getenv("ZIPKIN_SERVICE_PORT"));

        URLConnectionSender sender = URLConnectionSender.newBuilder()
                .endpoint(String.format("http://%s:%s/api/v2/spans", zipkinHost, zipkinPort))
                .build();

        return GrpcTracing.create(Tracing.newBuilder()
                .sampler(ALWAYS_SAMPLE)
                .spanReporter(AsyncReporter.create(sender))
                .build());
    }

}
