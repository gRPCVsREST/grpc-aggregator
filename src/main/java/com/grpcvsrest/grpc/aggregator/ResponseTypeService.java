package com.grpcvsrest.grpc.aggregator;

import com.grpcvsrest.grpc.ResponseType;
import com.grpcvsrest.grpc.ResponseTypeRequest;
import com.grpcvsrest.grpc.ResponseTypeResponse;
import com.grpcvsrest.grpc.ResponseTypeServiceGrpc.ResponseTypeServiceImplBase;
import io.grpc.stub.StreamObserver;

public class ResponseTypeService extends ResponseTypeServiceImplBase {

    private final IdRepository idRepository;

    public ResponseTypeService(IdRepository idRepository) {
        this.idRepository = idRepository;
    }

    @Override
    public void getResponseType(ResponseTypeRequest request, StreamObserver<ResponseTypeResponse> responseObserver) {
        ResponseType correctType = idRepository.typeByAggregationId(request.getAggrItemId());
        ResponseTypeResponse response = ResponseTypeResponse.newBuilder()
                .setId(request.getAggrItemId())
                .setType(correctType)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}
