package ru.moonlightapp.backend.core.storage.converter;

import jakarta.persistence.Converter;
import ru.moonlightapp.backend.core.model.attribute.ProductType;

@Converter(autoApply = true)
public final class ProductTypeConverter extends KeyedEnumConverterBase<ProductType> {

    @Override
    protected ProductType[] enumConstants() {
        return ProductType.values();
    }

}
