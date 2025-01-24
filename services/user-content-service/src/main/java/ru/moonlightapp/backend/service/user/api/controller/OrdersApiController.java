package ru.moonlightapp.backend.service.user.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.moonlightapp.backend.core.docs.annotation.BadRequestResponse;
import ru.moonlightapp.backend.core.docs.annotation.DescribeError;
import ru.moonlightapp.backend.core.docs.annotation.SuccessResponse;
import ru.moonlightapp.backend.core.exception.ApiException;
import ru.moonlightapp.backend.service.user.api.model.OrderItemModel;
import ru.moonlightapp.backend.service.user.api.service.OrdersService;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrdersApiController extends ApiControllerBase {

    private final OrdersService ordersService;

    @Operation(summary = "Создание заказов из корзины", tags = "orders-api")
    @SuccessResponse("Товар(ы) из корзины заказаны")
    @BadRequestResponse({"too_much_cart_items", "bad_cart_items", "missing_data"})
    @DescribeError(code = "too_much_cart_items", system = true, message = "Слишком много товаров", payload = "`maxItemsPerRequest`")
    @DescribeError(code = "bad_cart_items", system = true, message = "Товары указаны неверно", payload = "`badCartItemId`")
    @DescribeError(code = "missing_data", system = true, message = "Некоторые данные отсутствуют", payload = "`notFoundIds`")
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public List<OrderItemModel> addItems(
            @RequestParam(name = "cart_items") @NotEmpty String rawCartItemIds
    ) throws ApiException {
        return ordersService.addItems(getCurrentUsername(), rawCartItemIds);
    }

    @Operation(summary = "Получение страницы заказов", tags = "orders-api")
    @SuccessResponse(canBeEmpty = true)
    @GetMapping
    public ResponseEntity<Page<OrderItemModel>> getItems(
            @RequestParam(name = "page", defaultValue = "1") @Min(1) int pageNumber
    ) throws ApiException {
        Page<OrderItemModel> page = ordersService.findItems(getCurrentUsername(), pageNumber);
        return page.hasContent() ? ResponseEntity.ok(page) : ResponseEntity.noContent().build();
    }

}
