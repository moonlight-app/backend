package ru.moonlightapp.backend.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.moonlightapp.backend.model.attribute.ProductType;
import ru.moonlightapp.backend.storage.projection.ProductForeignProj;

import java.time.Instant;
import java.util.function.Function;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ForeignItemModel(
        @JsonProperty("item_id") long itemId,
        @JsonProperty("product_id") int productId,
        @JsonProperty("type") ProductType type,
        @JsonProperty("name") String name,
        @JsonProperty("price") float price,
        @JsonProperty("preview_url") String previewUrl,
        @JsonProperty("is_favorite") Boolean isFavorite,
        @JsonProperty("created_at") Instant createdAt
) {

    public static final int INCLUDE_TYPE = 1;
    public static final int INCLUDE_CREATED_AT = 4;

    public static ForeignItemModel from(ProductForeignProj proj, int flags, Function<Integer, Boolean> favoriteStateResolver) {
        return new ForeignItemModel(
                proj.itemId(),
                proj.productId(),
                ((flags & INCLUDE_TYPE) != 0) ? proj.type() : null,
                proj.name(),
                proj.price(),
                proj.previewUrl(),
                (favoriteStateResolver != null) ? favoriteStateResolver.apply(proj.productId()) : null,
                ((flags & INCLUDE_CREATED_AT) != 0) ? proj.createdAt() : null
        );
    }

}
