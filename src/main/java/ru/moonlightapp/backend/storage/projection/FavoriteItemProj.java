package ru.moonlightapp.backend.storage.projection;

import ru.moonlightapp.backend.model.attribute.ProductType;

import java.time.Instant;

public record FavoriteItemProj(
        long itemId,
        int productId,
        ProductType type,
        String name,
        float price,
        String previewUrl,
        Instant createdAt
) {

}
