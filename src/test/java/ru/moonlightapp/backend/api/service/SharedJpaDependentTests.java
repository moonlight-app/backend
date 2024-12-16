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

        assertEquals(9, page.getTotalElements());
        assertTrue(page.getContent().get(2).isFavorite());
        assertEquals(20, page.getContent().get(3).productId());
        assertEquals(1490F, page.getContent().get(6).price());
    }

    @Test
    void whenConstructCategoryMetadataForRings_thenSuccess() {
        CategoryMetadataModel model = assertDoesNotThrow(() -> catalogService.constructCategoryMetadata(ProductType.RING));

        assertEquals(708F, model.priceRange().min());
        assertEquals(199990F, model.priceRange().max());
        assertEquals(10, model.popularSizes().length);
        assertEquals(17F, model.popularSizes()[3]);
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

        assertEquals(1L, page.getTotalElements());
        assertEquals(197, page.getContent().getFirst().id());
    }

    @Test
    void whenFindFavoriteItems_thenSuccess() {
        Page<FavoriteItemModel> page = assertDoesNotThrow(() -> favoritesService.findItems("test2@test.com", 1));

        assertEquals(2, page.getTotalElements());
        assertEquals(52, page.getContent().getFirst().productId());
        assertEquals(2190F, page.getContent().getLast().price());
    }

    @Test
    void whenFindOrderItems_thenSuccess() {
        Page<OrderItemModel> page = assertDoesNotThrow(() -> ordersService.findItems("test@test.com", 1));

        assertEquals(1, page.getTotalElements());
        assertEquals("40-45", page.getContent().getFirst().size());
    }

}
