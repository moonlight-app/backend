package ru.moonlightapp.backend.core.storage.converter;

import jakarta.persistence.Converter;
import ru.moonlightapp.backend.core.model.attribute.Sex;

@Converter(autoApply = true)
public final class SexConverter extends KeyedEnumConverterBase<Sex> {

    @Override
    protected Sex[] enumConstants() {
        return Sex.values();
    }

}
