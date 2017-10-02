package com.grpcvsrest.grpc.aggregator;

import com.google.common.collect.Maps;
import com.grpcvsrest.grpc.ResponseType;

import java.util.Objects;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class AggregationIdGenerator implements IdGenerator {

    private final ConcurrentMap<Integer, SourceId> aggrIdToSourceId = Maps.newConcurrentMap();
    private final ConcurrentMap<SourceId, Integer> sourceIdToAggrId = Maps.newConcurrentMap();

    private final AtomicInteger generator = new AtomicInteger(10_000);

    @Override
    public int aggregationId(int id, ResponseType type) {
        SourceId sourceId = new SourceId(id, type);
        Integer existingAggrId = sourceIdToAggrId.get(sourceId);

        return existingAggrId != null? existingAggrId : generate(sourceId);
    }

    private int generate(SourceId sourceId) {
        int generatedId = generator.getAndIncrement();
        Integer existingAggrId = sourceIdToAggrId.putIfAbsent(sourceId, generatedId);
        int result;
        if (existingAggrId != null) {
            result = existingAggrId;
        } else {
            aggrIdToSourceId.put(generatedId, sourceId);
            result = generatedId;
        }
        return result;
    }

    private final class SourceId {
        private final int id;
        private final ResponseType type;

        private SourceId(int id, ResponseType type) {
            this.id = id;
            this.type = type;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, type);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            final SourceId other = (SourceId) obj;
            return Objects.equals(this.id, other.id)
                    && Objects.equals(this.type, other.type);
        }
    }
}
