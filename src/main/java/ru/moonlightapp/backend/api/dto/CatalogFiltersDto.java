package ru.moonlightapp.backend.api.dto;

import lombok.Builder;

@Builder(setterPrefix = "with")
public record CatalogFiltersDto(
        Float minPrice,
        Float maxPrice,
        String sizes,
        Integer audiences,
        Integer materials,
        Integer treasures
) {

}
