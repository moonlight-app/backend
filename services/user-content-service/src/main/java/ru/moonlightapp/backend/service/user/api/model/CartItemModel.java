package ru.moonlightapp.backend.service.user.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.moonlightapp.backend.core.model.attribute.ProductType;
import ru.moonlightapp.backend.service.user.storage.model.CartItem;
import ru.moonlightapp.backend.service.user.storage.model.Product;
import ru.moonlightapp.backend.service.user.storage.projection.CartItemProj;

import java.time.Instant;
import java.util.function.Function;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CartItemModel(
        @JsonProperty("item_id") long itemId,
        @JsonProperty("product_id") int productId,
        @JsonProperty("type") ProductType type,
        @JsonProperty("name") String name,
        @JsonProperty("price") float price,
        @JsonProperty("size") String size,
        @JsonProperty("count") int count,
        @JsonProperty("preview_url") String previewUrl,
        @JsonProperty("is_favorite") boolean isFavorite,
        @JsonProperty("created_at") Instant createdAt
) {

    public static CartItemModel from(CartItem item, Product product, Function<Integer, Boolean> favoriteStateResolver) {
        return new CartItemModel(
                item.getId(),
                product.getId(),
                product.getType(),
                product.getName(),
                product.getPrice(),
                item.getSize(),
                item.getCount(),
                product.getPreviewUrl(),
                favoriteStateResolver.apply(product.getId()),
                item.getCreatedAt()
        );
    }

    public static CartItemModel from(CartItemProj proj, Function<Integer, Boolean> favoriteStateResolver) {
        return new CartItemModel(
                proj.itemId(),
                proj.productId(),
                proj.type(),
                proj.name(),
                proj.price(),
                proj.size(),
                proj.count(),
                proj.previewUrl(),
                favoriteStateResolver.apply(proj.productId()),
                proj.createdAt()
        );
    }

}
