package ru.moonlightapp.backend.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.moonlightapp.backend.storage.model.content.Product;

public record CatalogItemModel(
        @JsonProperty("id") long id,
        @JsonProperty("name") String name,
        @JsonProperty("price") float price,
        @JsonProperty("preview_url") String previewUrl
) {

    public static CatalogItemModel from(Product product) {
        return new CatalogItemModel(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getPreviewUrl()
        );
    }

}
