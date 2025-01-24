package ru.moonlightapp.backend.service.user.storage.projection;

import ru.moonlightapp.backend.core.model.attribute.OrderStatus;
import ru.moonlightapp.backend.core.model.attribute.ProductType;

import java.time.Instant;

public record OrderItemProj(
        long itemId,
        int productId,
        ProductType type,
        String name,
        float price,
        String size,
        int count,
        String previewUrl,
        OrderStatus status,
        Instant createdAt
) {

}
