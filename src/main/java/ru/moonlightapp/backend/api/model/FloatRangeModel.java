package ru.moonlightapp.backend.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FloatRangeModel(
        @JsonProperty("min") float min,
        @JsonProperty("max") float max
) {

}
