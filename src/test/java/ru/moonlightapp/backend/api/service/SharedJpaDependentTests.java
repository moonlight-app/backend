package ru.moonlightapp.backend.api.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.test.context.jdbc.Sql;
import ru.moonlightapp.backend.SpringBootTests;
import ru.moonlightapp.backend.api.dto.CatalogFiltersDto;
import ru.moonlightapp.backend.api.model.*;
import ru.moonlightapp.backend.model.attribute.CatalogSorting;
import ru.moonlightapp.backend.model.attribute.ProductType;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureTestDatabase
@AutoConfigureTestEntityManager
@Sql(value = {"/test-data-dump.sql", "/test-data-api.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
public final class SharedJpaDependentTests extends SpringBootTests {

    @Autowired private CartService cartService;
    @Autowired private CatalogService catalogService;
    @Autowired private FavoritesService favoritesService;
    @Autowired private OrdersService ordersService;

    @Test
    void contextLoads() {
        assertNotNull(catalogService);
    }

    @Test
    void whenFindCartItems_thenSuccess() {
        Page<CartItemModel> page = assertDoesNotThrow(() -> cartService.findItems("test@test.com", 1));

        assertEquals(page.getTotalElements(), 9);
        assertTrue(page.getContent().get(2).isFavorite());
        assertEquals(page.getContent().get(3).productId(), 20);
        assertEquals(page.getContent().get(6).price(), 1490F);
    }

    @Test
    void whenConstructCategoryMetadataForRings_thenSuccess() {
        CategoryMetadataModel model = assertDoesNotThrow(() -> catalogService.constructCategoryMetadata(ProductType.RING));

        assertEquals(model.priceRange().min(), 708F);
        assertEquals(model.priceRange().max(), 199990F);
        assertEquals(model.popularSizes().length, 10);
        assertEquals(model.popularSizes()[3], 17F);
    }

    @Test
    void whenFindCatalogItemsWithTypeNecklaceAndMinPriceFilter_thenSuccess() {
        CatalogFiltersDto filtersDto = CatalogFiltersDto.builder()
                .withMinPrice(10000F)
                .build();

        Page<CatalogItemModel> page = assertDoesNotThrow(() -> catalogService.findItems(
                ProductType.NECKLACE,
                filtersDto,
                CatalogSorting.POPULARITY,
                1,
                null
        ));

        assertEquals(page.getTotalElements(), 1L);
        assertEquals(page.getContent().getFirst().id(), 197);
    }

    @Test
    void whenFindFavoriteItems_thenSuccess() {
        Page<FavoriteItemModel> page = assertDoesNotThrow(() -> favoritesService.findItems("test2@test.com", 1));

        assertEquals(page.getTotalElements(), 2);
        assertEquals(page.getContent().getFirst().productId(), 52);
        assertEquals(page.getContent().getLast().price(), 2190F);
    }

    @Test
    void whenFindOrderItems_thenSuccess() {
        Page<OrderItemModel> page = assertDoesNotThrow(() -> ordersService.findItems("test@test.com", 1));

        assertEquals(page.getTotalElements(), 1);
        assertEquals(page.getContent().getFirst().size(), "40-45");
    }

}
