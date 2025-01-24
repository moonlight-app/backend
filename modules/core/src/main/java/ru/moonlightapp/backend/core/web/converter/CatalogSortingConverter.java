package ru.moonlightapp.backend.core.web.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.moonlightapp.backend.core.model.attribute.CatalogSorting;

@Component
public final class CatalogSortingConverter implements Converter<String, CatalogSorting> {

    @Override
    public CatalogSorting convert(String value) {
        return CatalogSorting.findByKey(value).orElse(null);
    }

}
