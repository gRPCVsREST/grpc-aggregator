package com.grpcvsrest.grpc.aggregator;

import com.grpcvsrest.grpc.ContentStreamingServiceGrpc;
import com.grpcvsrest.grpc.aggregator.AggregationStreamingService.TypedStub;
import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.NettyServerBuilder;

import java.io.IOException;

import static com.grpcvsrest.grpc.ResponseType.PEREMOGA;
import static com.grpcvsrest.grpc.ResponseType.ZRADA;

/**
 * Starts gRPC server with {@link AggregationStreamingService} and {@link ResponseTypeService}.
 */
public class AggregationServer {

    public static void main(String[] args) throws IOException, InterruptedException {

        String peremogaHost = System.getenv("peremoga_host");
        int peremogaPort = Integer.valueOf(System.getenv("peremoga_port"));

        String zradaHost = System.getenv("zrada_host");
        int zradaPort = Integer.valueOf(System.getenv("zrada_port"));

        ManagedChannel peremogaChannel = NettyChannelBuilder.forAddress(peremogaHost, peremogaPort)
                .usePlaintext(true)
                .build();
        ManagedChannel zradaChannel = NettyChannelBuilder.forAddress(zradaHost, zradaPort)
                .usePlaintext(true)
                .build();

        TypedStub peremogaStub = new TypedStub(ContentStreamingServiceGrpc.newStub(peremogaChannel), PEREMOGA);
        TypedStub zradaStub = new TypedStub(ContentStreamingServiceGrpc.newStub(zradaChannel), ZRADA);

        AggregationIdRepository idRepository = new AggregationIdRepository();
        Server grpcServer = NettyServerBuilder.forPort(8080)
                .addService(new AggregationStreamingService(idRepository, peremogaStub, zradaStub))
                .addService(new ResponseTypeService(idRepository)).build()
                .start();

        Runtime.getRuntime().addShutdownHook(new Thread(grpcServer::shutdown));
        grpcServer.awaitTermination();
    }

}
