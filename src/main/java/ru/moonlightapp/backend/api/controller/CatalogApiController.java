package ru.moonlightapp.backend.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.moonlightapp.backend.api.dto.CatalogFiltersDto;
import ru.moonlightapp.backend.api.model.CatalogItemModel;
import ru.moonlightapp.backend.api.model.CategoryMetadataModel;
import ru.moonlightapp.backend.api.service.CatalogService;
import ru.moonlightapp.backend.docs.annotation.SuccessResponse;
import ru.moonlightapp.backend.exception.ApiException;
import ru.moonlightapp.backend.model.attribute.CatalogSorting;
import ru.moonlightapp.backend.model.attribute.ProductType;

@RestController
@RequestMapping("/api/catalog")
@RequiredArgsConstructor
public class CatalogApiController extends ApiControllerBase {

    private final CatalogService catalogService;

    @Operation(summary = "Получение мета-данных категории", tags = "catalog-api")
    @GetMapping("/metadata/{productType}")
    public CategoryMetadataModel getCategoryMetadata(@PathVariable ProductType productType) {
        return catalogService.constructCategoryMetadata(productType);
    }

    @Operation(summary = "Получение страницы каталога", tags = "catalog-api")
    @SuccessResponse(canBeEmpty = true)
    @GetMapping("/items/{productType}")
    public ResponseEntity<Page<CatalogItemModel>> getItems(
            @PathVariable ProductType productType,
            @RequestParam(name = "sort_by", defaultValue = "popularity") CatalogSorting sorting,
            @RequestParam(name = "min_price", required = false) @Min(1) Float minPrice,
            @RequestParam(name = "max_price", required = false) @Min(1) Float maxPrice,
            @RequestParam(name = "sizes", required = false) String sizes,
            @RequestParam(name = "audiences", required = false) @Min(1) Integer audiences,
            @RequestParam(name = "materials", required = false) @Min(1) Integer materials,
            @RequestParam(name = "treasures", required = false) @Min(1) Integer treasures,
            @RequestParam(name = "page", defaultValue = "1") @Min(1) int pageNumber
    ) throws ApiException {
        CatalogFiltersDto filtersDto = new CatalogFiltersDto(minPrice, maxPrice, sizes, audiences, materials, treasures);
        Page<CatalogItemModel> page = catalogService.findItems(productType, filtersDto, sorting, pageNumber);
        return page.hasContent() ? ResponseEntity.ok(page) : ResponseEntity.noContent().build();
    }

}
