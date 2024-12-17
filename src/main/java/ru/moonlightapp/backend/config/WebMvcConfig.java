package ru.moonlightapp.backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.moonlightapp.backend.web.converter.CatalogSortingConverter;
import ru.moonlightapp.backend.web.converter.ProductTypeConverter;
import ru.moonlightapp.backend.web.converter.SexConverter;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final CatalogSortingConverter catalogSortingConverter;
    private final ProductTypeConverter productTypeConverter;
    private final SexConverter sexConverter;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(catalogSortingConverter);
        registry.addConverter(productTypeConverter);
        registry.addConverter(sexConverter);
    }

}