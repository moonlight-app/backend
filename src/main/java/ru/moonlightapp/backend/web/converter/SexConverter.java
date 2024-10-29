package ru.moonlightapp.backend.web.converter;

import org.springframework.core.convert.converter.Converter;
import ru.moonlightapp.backend.model.attribute.Sex;

public final class SexConverter implements Converter<String, Sex> {

    @Override
    public Sex convert(String sex) {
        return Sex.findByKey(sex).orElseThrow();
    }

}
