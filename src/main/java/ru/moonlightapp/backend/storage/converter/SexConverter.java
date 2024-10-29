package ru.moonlightapp.backend.storage.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ru.moonlightapp.backend.model.attribute.Sex;

@Converter(autoApply = true)
public class SexConverter implements AttributeConverter<Sex, String> {

    @Override
    public String convertToDatabaseColumn(Sex sex) {
        return sex != null ? sex.getKey() : null;
    }

    @Override
    public Sex convertToEntityAttribute(String key) {
        return Sex.findByKey(key).orElse(null);
    }

}
