package ru.moonlightapp.backend.core.web.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.moonlightapp.backend.core.model.attribute.Sex;

@Component
public final class SexConverter implements Converter<String, Sex> {

    @Override
    public Sex convert(String value) {
        return Sex.findByKey(value).orElse(null);
    }

}
