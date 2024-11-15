package ru.moonlightapp.backend.api.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.moonlightapp.backend.exception.ApiException;
import ru.moonlightapp.backend.storage.model.content.OrderItem;
import ru.moonlightapp.backend.storage.projection.CartItemProj;
import ru.moonlightapp.backend.storage.repository.content.OrderItemRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public final class OrdersServiceTests {

    @MockBean private OrderItemRepository orderItemRepository;
    @MockBean private CartService cartService;

    @Autowired private EntityManager entityManager;
    @Autowired private OrdersService ordersService;

    @Test
    void contextLoads() {
        assertNotNull(orderItemRepository);
        assertNotNull(cartService);
        assertNotNull(ordersService);
    }

    @Test
    void whenAddItemsWithTooMuchItemsToAdd_thenThrowsApiException() {
        String email = "test@test.com";
        String rawCartItemIds = IntStream.rangeClosed(1, 11)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining(","));

        ApiException exception = assertThrows(
                ApiException.class,
                () -> ordersService.addItems(email, rawCartItemIds)
        );

        assertEquals(exception.getErrorCode(), "too_much_cart_items");
    }

    @Test
    void whenAddItemsWithBadItemToAdd_thenThrowsApiException() {
        String email = "test@test.com";
        String rawCartItemIds = IntStream.rangeClosed(1, 5)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining(","));

        String finalRawCartItemIds = rawCartItemIds + ",aaa,6";

        ApiException exception = assertThrows(
                ApiException.class,
                () -> ordersService.addItems(email, finalRawCartItemIds)
        );

        assertEquals(exception.getErrorCode(), "bad_cart_items");
    }

    @Test
    void whenAddItemsWithMissingData_thenThrowsApiException() {
        String email = "test@test.com";
        String rawCartItemIds = IntStream.rangeClosed(1, 5)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining(","));

        List<CartItemProj> cartItemProjs = IntStream.of(1, 3, 4)
                .mapToObj(id -> CartItemProj.builder().withItemId(id).build())
                .toList();

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        Set<Long> cartItemIds = LongStream.rangeClosed(1L, 5L).boxed().collect(Collectors.toSet());
        when(cartService.queryOwnedItems(builder, email, cartItemIds)).thenReturn(cartItemProjs);

        ApiException exception = assertThrows(
                ApiException.class,
                () -> ordersService.addItems(email, rawCartItemIds)
        );

        assertEquals(exception.getErrorCode(), "missing_data");
    }

    @Test
    void whenAddItemsWithCorrectData_thenSuccess() {
        String email = "test@test.com";
        String rawCartItemIds = IntStream.rangeClosed(1, 5)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining(","));

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        Set<Long> cartItemIds = LongStream.rangeClosed(1L, 5L).boxed().collect(Collectors.toSet());
        List<CartItemProj> cartItemProjs = cartItemIds.stream().map(id -> CartItemProj.builder().withItemId(id).build()).toList();
        when(cartService.queryOwnedItems(builder, email, cartItemIds)).thenReturn(cartItemProjs);

        assertDoesNotThrow(() -> ordersService.addItems(email, rawCartItemIds));

        verify(cartService, times(1)).removeItems(anyIterable());
        verify(orderItemRepository, times(1)).saveAll(anyIterable());
    }

    @Test
    void whenCloseOrdersWithNoOrdersToClose_thenSuccess() {
        when(orderItemRepository.findCloseableOrders(0L)).thenReturn(List.of());
        ordersService.closeOrders(0L);
        verify(orderItemRepository, never()).saveAll(anyIterable());
    }

    @Test
    void whenCloseOrdersWithOrdersToClose_thenSuccess() {
        when(orderItemRepository.findCloseableOrders(0L)).thenReturn(List.of(new OrderItem(), new OrderItem()));
        ordersService.closeOrders(0L);
        verify(orderItemRepository, times(1)).saveAll(anyIterable());
    }

}
