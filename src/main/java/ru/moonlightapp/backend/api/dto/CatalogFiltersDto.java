package ru.moonlightapp.backend.api.dto;

public record CatalogFiltersDto(
        Float minPrice,
        Float maxPrice,
        String sizes,
        Integer audiences,
        Integer materials,
        Integer treasures
) {

}
