package ru.moonlightapp.backend.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.moonlightapp.backend.api.model.CartItemModel;
import ru.moonlightapp.backend.api.service.CartService;
import ru.moonlightapp.backend.docs.annotation.BadRequestResponse;
import ru.moonlightapp.backend.docs.annotation.DescribeError;
import ru.moonlightapp.backend.docs.annotation.SuccessResponse;
import ru.moonlightapp.backend.exception.ApiException;

@DescribeError(code = "cart_item_not_found", message = "Товар в корзине не найден")
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartApiController extends ApiControllerBase {

    private final CartService cartService;

    @Operation(summary = "Добавление товара в корзину", tags = "cart-api")
    @SuccessResponse("Товар добавлен в корзину")
    @BadRequestResponse({"product_not_found", "product_size_required", "product_size_not_found"})
    @DescribeError(code = "product_not_found", system = true, message = "Товар не существует")
    @DescribeError(code = "product_size_required", system = true, message = "Необходим конкретный размер товара")
    @DescribeError(code = "product_size_not_found", system = true, message = "Товар не имеет указанного размера")
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public CartItemModel addItem(
            @RequestParam(name = "product_id") @Min(1) int productId,
            @RequestParam(name = "size", required = false) String size,
            @RequestParam(name = "count", defaultValue = "1") @Min(1) int count
    ) throws ApiException {
        return cartService.addItem(getCurrentUsername(), productId, size, count);
    }

    @Operation(summary = "Изменение количества товара", tags = "cart-api")
    @SuccessResponse("Количество товара в корзине изменено")
    @BadRequestResponse("cart_item_not_found")
    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    public void changeCount(
            @RequestParam(name = "item_id") @Min(1) long itemId,
            @RequestParam(name = "count", defaultValue = "1") @Min(1) int count
    ) throws ApiException {
        cartService.changeCount(getCurrentUsername(), itemId, count);
    }

    @Operation(summary = "Получение страницы корзины", tags = "cart-api")
    @SuccessResponse(canBeEmpty = true)
    @GetMapping
    public ResponseEntity<Page<CartItemModel>> getItems(
            @RequestParam(name = "page", defaultValue = "1") @Min(1) int pageNumber
    ) throws ApiException {
        Page<CartItemModel> page = cartService.findItems(getCurrentUsername(), pageNumber);
        return page.hasContent() ? ResponseEntity.ok(page) : ResponseEntity.noContent().build();
    }

    @Operation(summary = "Удаление товара из корзины", tags = "cart-api")
    @SuccessResponse("Товар удален из корзины")
    @BadRequestResponse("cart_item_not_found")
    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void removeItem(
            @RequestParam(name = "item_id") @Min(1) long itemId
    ) throws ApiException {
        cartService.removeItem(getCurrentUsername(), itemId);
    }

}
