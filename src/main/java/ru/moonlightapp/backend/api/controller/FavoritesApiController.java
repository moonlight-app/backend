package ru.moonlightapp.backend.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.moonlightapp.backend.api.model.FavoriteItemModel;
import ru.moonlightapp.backend.api.service.FavoritesService;
import ru.moonlightapp.backend.docs.annotation.BadRequestResponse;
import ru.moonlightapp.backend.docs.annotation.DescribeError;
import ru.moonlightapp.backend.docs.annotation.SuccessResponse;
import ru.moonlightapp.backend.exception.ApiException;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoritesApiController extends ApiControllerBase {

    private final FavoritesService favoritesService;

    @Operation(summary = "Добавление товара в избранные", tags = "favorites-api")
    @SuccessResponse("Товар добавлен в избранные")
    @BadRequestResponse({"product_not_found", "favorite_item_already_exists"})
    @DescribeError(code = "product_not_found", system = true, message = "Товар не существует")
    @DescribeError(code = "favorite_item_already_exists", message = "Товар уже в избранных")
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public FavoriteItemModel addItem(
            @RequestParam(name = "product_id") @Min(1) int productId
    ) throws ApiException {
        return favoritesService.addItem(getCurrentUsername(), productId);
    }

    @Operation(summary = "Получение страницы избранных", tags = "favorites-api")
    @SuccessResponse(canBeEmpty = true)
    @GetMapping
    public ResponseEntity<Page<FavoriteItemModel>> getItems(
            @RequestParam(name = "page", defaultValue = "1") @Min(1) int pageNumber
    ) throws ApiException {
        Page<FavoriteItemModel> page = favoritesService.findItems(getCurrentUsername(), pageNumber);
        return page.hasContent() ? ResponseEntity.ok(page) : ResponseEntity.noContent().build();
    }

    @Operation(summary = "Удаление товара из избранных", tags = "favorites-api")
    @SuccessResponse("Товар удален из избранных")
    @BadRequestResponse({"favorite_item_not_found"})
    @DescribeError(code = "favorite_item_not_found", message = "Избранный товар не найден")
    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void removeItem(
            @RequestParam(name = "item_id") @Min(1) long itemId
    ) throws ApiException {
        favoritesService.removeItem(getCurrentUsername(), itemId);
    }

}
