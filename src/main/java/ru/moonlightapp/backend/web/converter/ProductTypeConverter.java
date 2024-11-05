package ru.moonlightapp.backend.web.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.moonlightapp.backend.model.attribute.ProductType;

@Component
public final class ProductTypeConverter implements Converter<String, ProductType> {

    @Override
    public ProductType convert(String value) {
        return ProductType.findByKey(value).orElse(null);
    }

}
