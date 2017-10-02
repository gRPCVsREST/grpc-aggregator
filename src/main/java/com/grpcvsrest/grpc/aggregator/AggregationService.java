package com.grpcvsrest.grpc.aggregator;

import com.grpcvsrest.grpc.AggregationRequest;
import com.grpcvsrest.grpc.AggregationResponse;
import com.grpcvsrest.grpc.AggregationServiceGrpc;
import io.grpc.stub.StreamObserver;

/**
 * Implements gRPC Aggregation Service.
 */
public class AggregationService extends AggregationServiceGrpc.AggregationServiceImplBase {

    @Override
    public void get(AggregationRequest request, StreamObserver<AggregationResponse> responseObserver) {
        super.get(request, responseObserver);
    }
}
