package ru.moonlightapp.backend.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.moonlightapp.backend.storage.model.content.Product;

import java.util.function.Function;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CatalogItemModel(
        @JsonProperty("id") int id,
        @JsonProperty("name") String name,
        @JsonProperty("price") float price,
        @JsonProperty("preview_url") String previewUrl,
        @JsonProperty("is_favorite") Boolean isFavorite
) {

    public static CatalogItemModel from(Product product) {
        return from(product, null);
    }

    public static CatalogItemModel from(Product product, Function<Integer, Boolean> favoriteStateResolver) {
        return new CatalogItemModel(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getPreviewUrl(),
                (favoriteStateResolver != null) ? favoriteStateResolver.apply(product.getId()) : null
        );
    }

}
