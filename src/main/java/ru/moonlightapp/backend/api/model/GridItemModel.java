package ru.moonlightapp.backend.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.moonlightapp.backend.storage.model.content.Product;

public record GridItemModel(
        @JsonProperty("id") long id,
        @JsonProperty("name") String name,
        @JsonProperty("price") float price,
        @JsonProperty("preview_url") String previewUrl,
        @JsonProperty("is_favorite") Boolean isFavorite
) {

    public static GridItemModel from(Product product) {
        return from(product, null);
    }

    public static GridItemModel from(Product product, Boolean isFavorite) {
        return new GridItemModel(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getPreviewUrl(),
                isFavorite
        );
    }

}
