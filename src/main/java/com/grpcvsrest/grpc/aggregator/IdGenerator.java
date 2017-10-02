package com.grpcvsrest.grpc.aggregator;

import com.grpcvsrest.grpc.ResponseType;

/**
 * Generates aggregation id based on source id and type.
 */
public interface IdGenerator {

    int aggregationId(int sourceId, ResponseType type);

}
