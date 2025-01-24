package ru.moonlightapp.backend.service.user.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.moonlightapp.backend.core.model.attribute.OrderStatus;
import ru.moonlightapp.backend.core.model.attribute.ProductType;
import ru.moonlightapp.backend.service.user.storage.model.OrderItem;
import ru.moonlightapp.backend.service.user.storage.projection.CartItemProj;
import ru.moonlightapp.backend.service.user.storage.projection.OrderItemProj;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record OrderItemModel(
        @JsonProperty("item_id") long itemId,
        @JsonProperty("product_id") int productId,
        @JsonProperty("type") ProductType type,
        @JsonProperty("name") String name,
        @JsonProperty("price") float price,
        @JsonProperty("size") String size,
        @JsonProperty("count") int count,
        @JsonProperty("preview_url") String previewUrl,
        @JsonProperty("status") OrderStatus status,
        @JsonProperty("created_at") Instant createdAt
) {

    public static OrderItemModel from(OrderItem item, CartItemProj proj) {
        return new OrderItemModel(
                item.getId(),
                proj.productId(),
                proj.type(),
                proj.name(),
                proj.price(),
                item.getSize(),
                item.getCount(),
                proj.previewUrl(),
                item.getStatus(),
                item.getCreatedAt()
        );
    }

    public static OrderItemModel from(OrderItemProj proj) {
        return new OrderItemModel(
                proj.itemId(),
                proj.productId(),
                proj.type(),
                proj.name(),
                proj.price(),
                proj.size(),
                proj.count(),
                proj.previewUrl(),
                proj.status(),
                proj.createdAt()
        );
    }

}
