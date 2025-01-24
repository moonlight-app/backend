package ru.moonlightapp.backend.core.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.moonlightapp.backend.core.protobuf.model.FloatRange.ProtoFloatRange;

import java.util.Optional;

public record FloatRangeModel(
        @JsonProperty("min") float min,
        @JsonProperty("max") float max
) {

    public static Optional<FloatRangeModel> fromProto(ProtoFloatRange floatRange) {
        if (floatRange == null)
            return Optional.empty();

        return Optional.of(new FloatRangeModel(
                floatRange.getMin(),
                floatRange.getMax()
        ));
    }

    public static ProtoFloatRange toProto(FloatRangeModel floatRange) {
        if (floatRange == null)
            return null;

        return floatRange.toProto();
    }

    public ProtoFloatRange toProto() {
        return ProtoFloatRange.newBuilder()
                .setMin(min)
                .setMax(max)
                .build();
    }

}
