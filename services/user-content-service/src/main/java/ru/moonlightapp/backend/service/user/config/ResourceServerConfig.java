package ru.moonlightapp.backend.service.user.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.moonlightapp.backend.core.web.converter.CatalogSortingConverter;
import ru.moonlightapp.backend.core.web.converter.ProductTypeConverter;
import ru.moonlightapp.backend.core.web.converter.SexConverter;

@Configuration
@Import(ConverterConfig.class)
@RequiredArgsConstructor
@Profile("!test")
public class ResourceServerConfig implements WebMvcConfigurer {

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