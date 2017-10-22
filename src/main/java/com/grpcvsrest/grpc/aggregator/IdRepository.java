package com.grpcvsrest.grpc.aggregator;

import com.grpcvsrest.grpc.ResponseType;

import javax.annotation.Nullable;

/**
 * Generates aggregation id based on source id and type.
 */
public interface IdRepository {

    int aggregationId(int sourceId, ResponseType type);

    @Nullable
    ResponseType typeByAggregationId(int aggregationId);

}
