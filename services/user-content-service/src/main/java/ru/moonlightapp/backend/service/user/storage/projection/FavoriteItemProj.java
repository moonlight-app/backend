package ru.moonlightapp.backend.service.user.storage.projection;

import ru.moonlightapp.backend.core.model.attribute.ProductType;

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
