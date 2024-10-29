package ru.moonlightapp.backend.storage.converter;

import jakarta.persistence.Converter;
import ru.moonlightapp.backend.model.attribute.Sex;

@Converter(autoApply = true)
public class SexConverter extends KeyedEnumConverterBase<Sex> {

    @Override
    protected Sex[] enumConstants() {
        return Sex.values();
    }

}
