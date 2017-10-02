package com.grpcvsrest.grpc.aggregator;

import com.grpcvsrest.grpc.AggregationStreamingRequest;
import com.grpcvsrest.grpc.AggregationStreamingResponse;
import com.grpcvsrest.grpc.AggregationStreamingServiceGrpc;
import com.grpcvsrest.grpc.AggregationStreamingServiceGrpc.AggregationStreamingServiceStub;
import io.grpc.ManagedChannel;
import io.grpc.netty.NettyChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.Semaphore;

public class AggregationClient {

    public static void main(String... args) throws InterruptedException {
        int port = 18080;
        ManagedChannel channel = NettyChannelBuilder.forAddress("localhost", port)
                .usePlaintext(true).build();

        AggregationStreamingServiceStub stub = AggregationStreamingServiceGrpc.newStub(channel);

        Semaphore exitSemaphore = new Semaphore(0);
        stub.subscribe(
                AggregationStreamingRequest.getDefaultInstance(),
                new StreamObserver<AggregationStreamingResponse>() {
                    @Override
                    public void onNext(AggregationStreamingResponse response) {
                        System.out.printf("Content: %s. id %s. type: %s.%n", response.getContent(),
                                response.getId(),
                                response.getType());
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        exitSemaphore.release();
                    }

                    @Override
                    public void onCompleted() {
                        System.out.println("Call completed!");
                        exitSemaphore.release();
                    }
                });

        exitSemaphore.acquire();
    }
}
