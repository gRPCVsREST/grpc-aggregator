package com.grpcvsrest.grpc.aggregator;

import com.grpcvsrest.grpc.*;
import com.grpcvsrest.grpc.AggregationStreamingServiceGrpc.AggregationStreamingServiceImplBase;
import com.grpcvsrest.grpc.ContentStreamingServiceGrpc.ContentStreamingServiceStub;
import io.grpc.stub.StreamObserver;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

/**
 * Implements streaming gRPC Aggregation Service.
 */
public class AggregationStreamingService extends AggregationStreamingServiceImplBase {

    private final IdGenerator idGenerator;
    private final Collection<TypedStub> stubs;

    public AggregationStreamingService(IdGenerator idGenerator, TypedStub... stubs) {
        this.idGenerator = idGenerator;
        this.stubs = Arrays.asList(stubs);
    }

    @Override
    public void subscribe(AggregationStreamingRequest request,
                          StreamObserver<AggregationStreamingResponse> responseObserver) {

        CompletableFuture<?>[] allCompleted = stubs.stream()
                .map(stub -> Pair.of(stub, new CompletableFuture<Void>()))
                .map(typedStubAndFuture -> {
                    TypedStub typedStub = typedStubAndFuture.getKey();
                    CompletableFuture<Void> streamCompleted = typedStubAndFuture.getValue();

                    typedStub.stub.subscribe(ContentStreamingRequest.getDefaultInstance(),
                            new StreamObserver<ContentStreamingResponse>() {
                                @Override
                                public void onNext(ContentStreamingResponse response) {
                                    int aggregationId = idGenerator.aggregationId(response.getId(), typedStub.type);

                                    AggregationStreamingResponse aggrResponse = AggregationStreamingResponse.newBuilder()
                                            .setId(aggregationId)
                                            .setContent(response.getContent())
                                            .setType(typedStub.type)
                                            .build();
                                    responseObserver.onNext(aggrResponse);
                                }

                                @Override
                                public void onError(Throwable error) {
                                    responseObserver.onError(error);
                                }

                                @Override
                                public void onCompleted() {
                                    streamCompleted.complete(null);
                                }
                            });
                    return streamCompleted;
                }).toArray(CompletableFuture[]::new);

        // Complete stream when all individual streams completed
        CompletableFuture.allOf(allCompleted).thenRun(responseObserver::onCompleted);
    }

    public static final class TypedStub {
        private final ContentStreamingServiceStub stub;
        private final ResponseType type;

        public TypedStub(ContentStreamingServiceStub stub, ResponseType type) {
            this.stub = stub;
            this.type = type;
        }
    }

}
