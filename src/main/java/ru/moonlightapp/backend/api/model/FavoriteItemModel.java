package ru.moonlightapp.backend.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.moonlightapp.backend.model.attribute.ProductType;
import ru.moonlightapp.backend.storage.projection.FavoriteItemProj;

import java.time.Instant;

public record FavoriteItemModel(
        @JsonProperty("item_id") long itemId,
        @JsonProperty("product_id") int productId,
        @JsonProperty("type") ProductType type,
        @JsonProperty("name") String name,
        @JsonProperty("price") float price,
        @JsonProperty("preview_url") String previewUrl,
        @JsonProperty("created_at") Instant createdAt
) {

    public static FavoriteItemModel from(FavoriteItemProj proj) {
        return new FavoriteItemModel(
                proj.itemId(),
                proj.productId(),
                proj.type(),
                proj.name(),
                proj.price(),
                proj.previewUrl(),
                proj.createdAt()
        );
    }

}
