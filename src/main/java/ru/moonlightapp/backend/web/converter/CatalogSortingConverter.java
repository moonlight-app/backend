package ru.moonlightapp.backend.web.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.moonlightapp.backend.model.CatalogSorting;

@Component
public final class CatalogSortingConverter implements Converter<String, CatalogSorting> {

    @Override
    public CatalogSorting convert(String value) {
        return CatalogSorting.findByKey(value).orElse(null);
    }

}
