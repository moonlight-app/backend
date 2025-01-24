package ru.moonlightapp.backend.service.user.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.moonlightapp.backend.core.model.attribute.ProductType;
import ru.moonlightapp.backend.service.user.storage.model.Product;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ProductModel(
        @JsonProperty("id") int id,
        @JsonProperty("article") int article,
        @JsonProperty("type") ProductType type,
        @JsonProperty("name") String name,
        @JsonProperty("price") float price,
        @JsonProperty("sizes") String[] sizes,
        @JsonProperty("materials") Integer materials,
        @JsonProperty("sample") String sample,
        @JsonProperty("sample_type") String sampleType,
        @JsonProperty("treasures") Integer treasures,
        @JsonProperty("weight") Float weight,
        @JsonProperty("preview_url") String previewUrl,
        @JsonProperty("description") String description
) {

    public static ProductModel from(Product product) {
        return new ProductModel(
                product.getId(),
                product.getArticle(),
                product.getType(),
                product.getName(),
                product.getPrice(),
                product.getSizesAsArray(),
                product.getMaterials(),
                product.getSample(),
                product.getSampleType(),
                product.getTreasures(),
                product.getWeight(),
                product.getPreviewUrl(),
                product.getDescription()
        );
    }

}
