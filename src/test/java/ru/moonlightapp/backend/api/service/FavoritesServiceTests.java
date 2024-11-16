package ru.moonlightapp.backend.api.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.moonlightapp.backend.SpringBootTests;
import ru.moonlightapp.backend.exception.ApiException;
import ru.moonlightapp.backend.storage.model.content.FavoriteItem;
import ru.moonlightapp.backend.storage.model.content.Product;
import ru.moonlightapp.backend.storage.repository.content.FavoriteItemRepository;
import ru.moonlightapp.backend.storage.repository.content.ProductRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public final class FavoritesServiceTests extends SpringBootTests {

    @MockBean private FavoriteItemRepository favoriteItemRepository;
    @MockBean private ProductRepository productRepository;

    @Autowired private FavoritesService favoritesService;

    @Test
    void contextLoads() {
        assertNotNull(favoriteItemRepository);
        assertNotNull(productRepository);
        assertNotNull(favoritesService);
    }

    @Test
    void whenAddUnknownProduct_thenThrowsApiException() {
        String email = "test@test.com";

        ApiException exception = assertThrows(
                ApiException.class,
                () -> favoritesService.addItem(email, 1)
        );

        assertEquals(exception.getErrorCode(), "product_not_found");
    }

    @Test
    void whenAddAlreadyFavoritedProduct_thenThrowsApiException() {
        String email = "test@test.com";

        Product product = Product.builder()
                .withId(1)
                .build();

        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(favoriteItemRepository.existsByUserEmailAndProductId(email, 1)).thenReturn(true);

        ApiException exception = assertThrows(
                ApiException.class,
                () -> favoritesService.addItem(email, 1)
        );

        assertEquals(exception.getErrorCode(), "favorite_item_already_exists");
    }

    @Test
    void whenAddItem_thenSuccess() {
        String email = "test@test.com";

        Product product = Product.builder()
                .withId(1)
                .build();

        FavoriteItem favoriteItem = new FavoriteItem();

        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(favoriteItemRepository.save(favoriteItem)).thenReturn(favoriteItem);

        assertDoesNotThrow(() -> favoritesService.addItem(email, 1));

        verify(favoriteItemRepository, times(1)).save(any(FavoriteItem.class));
    }

    @Test
    void whenRemoveUnknownItem_thenThrowsApiException() {
        String email = "test@test.com";

        ApiException exception = assertThrows(
                ApiException.class,
                () -> favoritesService.removeItem(email, 1)
        );

        assertEquals(exception.getErrorCode(), "favorite_item_not_found");
    }

    @Test
    void whenRemoveKnownItem_thenSuccess() {
        String email = "test@test.com";

        when(favoriteItemRepository.existsByIdAndUserEmail(1, email)).thenReturn(true);

        assertDoesNotThrow(() -> favoritesService.removeItem(email, 1));

        verify(favoriteItemRepository, times(1)).deleteById(anyLong());
    }

}
