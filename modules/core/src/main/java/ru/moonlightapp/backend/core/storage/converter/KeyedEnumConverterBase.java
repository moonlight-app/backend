package ru.moonlightapp.backend.core.storage.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ru.moonlightapp.backend.core.model.attribute.KeyedEnum;
import ru.moonlightapp.backend.core.util.KeyedEnumConstantFinder;

@Converter
public abstract class KeyedEnumConverterBase<E extends KeyedEnum> implements AttributeConverter<E, String> {

    protected abstract E[] enumConstants();

    @Override
    public String convertToDatabaseColumn(E constant) {
        return constant != null ? constant.getKey() : null;
    }

    @Override
    public E convertToEntityAttribute(String key) {
        return KeyedEnumConstantFinder.findByKey(key, enumConstants()).orElse(null);
    }

}
