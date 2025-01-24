package ru.moonlightapp.backend.service.user.storage.projection;

import lombok.Builder;
import ru.moonlightapp.backend.core.model.attribute.ProductType;

import java.time.Instant;

@Builder(setterPrefix = "with")
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
