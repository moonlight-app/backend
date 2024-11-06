package ru.moonlightapp.backend.storage.converter;

import jakarta.persistence.Converter;
import ru.moonlightapp.backend.model.attribute.ProductType;

@Converter(autoApply = true)
public final class ProductTypeConverter extends KeyedEnumConverterBase<ProductType> {

    @Override
    protected ProductType[] enumConstants() {
        return ProductType.values();
    }

}
