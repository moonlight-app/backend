package ru.moonlightapp.backend.storage.projection;

import ru.moonlightapp.backend.model.attribute.ProductType;

import java.time.Instant;

public record CartItemProj(
        long itemId,
        int productId,
        ProductType type,
        String name,
        float price,
        String size,
        int count,
        String previewUrl,
        Instant createdAt
) {

}