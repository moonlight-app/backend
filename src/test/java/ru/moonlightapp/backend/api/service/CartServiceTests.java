package ru.moonlightapp.backend.api.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.moonlightapp.backend.SpringBootTests;
import ru.moonlightapp.backend.exception.ApiException;
import ru.moonlightapp.backend.storage.model.content.CartItem;
import ru.moonlightapp.backend.storage.model.content.Product;
import ru.moonlightapp.backend.storage.repository.content.CartItemRepository;
import ru.moonlightapp.backend.storage.repository.content.ProductRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public final class CartServiceTests extends SpringBootTests {

    @MockBean private CartItemRepository cartItemRepository;
    @MockBean private ProductRepository productRepository;
    @MockBean private FavoritesService favoritesService;

    @Autowired private CartService cartService;

    @Test
    void contextLoads() {
        assertNotNull(cartItemRepository);
        assertNotNull(productRepository);
        assertNotNull(favoritesService);
        assertNotNull(cartService);
    }

    @Test
    void whenAddUnknownProduct_thenThrowsApiException() {
        String email = "test@test.com";

        ApiException exception = assertThrows(
                ApiException.class,
                () -> cartService.addItem(email, 1, null, 1)
        );

        assertEquals("product_not_found", exception.getErrorCode());
    }

    @Test
    void whenAddSizedProductWithNoSize_thenThrowsApiException() {
        String email = "test@test.com";

        Product product = Product.builder()
                .withId(1)
                .withSizes("11,12,13")
                .build();

        when(productRepository.findById(1)).thenReturn(Optional.of(product));

        ApiException exception = assertThrows(
                ApiException.class,
                () -> cartService.addItem(email, 1, null, 1)
        );

        assertEquals("product_size_required", exception.getErrorCode());
    }

    @Test
    void whenAddSizedProductWithUnknownSize_thenThrowsApiException() {
        String email = "test@test.com";

        Product product = Product.builder()
                .withId(1)
                .withSizes("11,12,13")
                .build();

        when(productRepository.findById(1)).thenReturn(Optional.of(product));

        ApiException exception = assertThrows(
                ApiException.class,
                () -> cartService.addItem(email, 1, "14", 1)
        );

        assertEquals("product_size_not_found", exception.getErrorCode());
    }

    @Test
    void whenAddSizedProductWithoutSize_thenSuccess() {
        String email = "test@test.com";

        Product product = Product.builder()
                .withId(1)
                .build();

        CartItem cartItem = new CartItem();

        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(cartItemRepository.save(cartItem)).thenReturn(cartItem);

        assertDoesNotThrow(() -> cartService.addItem(email, 1, null, 1));

        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    void whenAddSizedProductWithSize_thenSuccess() {
        String email = "test@test.com";

        Product product = Product.builder()
                .withId(1)
                .withSizes("11,12,13")
                .build();

        CartItem cartItem = new CartItem();

        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(cartItemRepository.save(cartItem)).thenReturn(cartItem);

        assertDoesNotThrow(() -> cartService.addItem(email, 1, "12", 1));

        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    void whenChangeCountOfUnknownItem_thenThrowsApiException() {
        String email = "test@test.com";

        ApiException exception = assertThrows(
                ApiException.class,
                () -> cartService.changeCount(email, 1, 1)
        );

        assertEquals("cart_item_not_found", exception.getErrorCode());
    }

    @Test
    void whenChangeCountOfKnownItemWithNewValue_thenSuccess() {
        String email = "test@test.com";

        CartItem cartItem = CartItem.builder()
                .withCount(1)
                .build();

        when(cartItemRepository.findByIdAndUserEmail(1, email)).thenReturn(Optional.of(cartItem));

        assertDoesNotThrow(() -> cartService.changeCount(email, 1, 2));

        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    void whenChangeCountOfKnownItemWithSameValue_thenSuccess() {
        String email = "test@test.com";

        CartItem cartItem = CartItem.builder()
                .withCount(1)
                .build();

        when(cartItemRepository.findByIdAndUserEmail(1, email)).thenReturn(Optional.of(cartItem));

        assertDoesNotThrow(() -> cartService.changeCount(email, 1, 1));

        verify(cartItemRepository, never()).save(any(CartItem.class));
    }

    @Test
    void whenRemoveUnknownItem_thenThrowsApiException() {
        String email = "test@test.com";

        ApiException exception = assertThrows(
                ApiException.class,
                () -> cartService.removeItem(email, 1)
        );

        assertEquals("cart_item_not_found", exception.getErrorCode());
    }

    @Test
    void whenRemoveKnownItem_thenSuccess() {
        String email = "test@test.com";

        when(cartItemRepository.existsByIdAndUserEmail(1, email)).thenReturn(true);

        assertDoesNotThrow(() -> cartService.removeItem(email, 1));

        verify(cartItemRepository, times(1)).deleteById(anyLong());
    }

}
