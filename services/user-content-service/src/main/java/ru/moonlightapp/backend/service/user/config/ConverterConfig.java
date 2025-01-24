package ru.moonlightapp.backend.service.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.moonlightapp.backend.core.web.converter.CatalogSortingConverter;
import ru.moonlightapp.backend.core.web.converter.ProductTypeConverter;
import ru.moonlightapp.backend.core.web.converter.SexConverter;

@Configuration
public class ConverterConfig {

    @Bean
    public CatalogSortingConverter catalogSortingConverter() {
        return new CatalogSortingConverter();
    }

    @Bean
    public ProductTypeConverter productTypeConverter() {
        return new ProductTypeConverter();
    }

    @Bean
    public SexConverter sexConverter() {
        return new SexConverter();
    }

}
