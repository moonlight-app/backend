package ru.moonlightapp.backend.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.moonlightapp.backend.model.attribute.ProductType;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CategoryMetadataModel(
        @JsonProperty("product_type") ProductType productType,
        @JsonProperty("price_range") FloatRangeModel priceRange,
        @JsonProperty("popular_sizes") float[] popularSizes
) {

}
