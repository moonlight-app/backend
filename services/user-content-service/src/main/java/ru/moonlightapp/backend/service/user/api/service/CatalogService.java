package ru.moonlightapp.backend.service.user.api.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.moonlightapp.backend.core.exception.ApiException;
import ru.moonlightapp.backend.core.model.FloatRangeModel;
import ru.moonlightapp.backend.core.model.attribute.CatalogSorting;
import ru.moonlightapp.backend.core.model.attribute.ProductType;
import ru.moonlightapp.backend.service.user.api.dto.CatalogFiltersDto;
import ru.moonlightapp.backend.service.user.api.model.CatalogItemModel;
import ru.moonlightapp.backend.service.user.api.model.CategoryMetadataModel;
import ru.moonlightapp.backend.service.user.api.model.ProductModel;
import ru.moonlightapp.backend.service.user.storage.model.*;
import ru.moonlightapp.backend.service.user.storage.repository.ProductRepository;
import ru.moonlightapp.backend.service.user.storage.specification.ProductSpecs;

import java.util.*;

@Service
@RequiredArgsConstructor
public final class CatalogService {

    private static final int POPULAR_SIZES_LIMIT = 10;
    private static final int ITEMS_PER_PAGE = 20;

    private final EntityManager entityManager;
    private final FavoritesService favoritesService;

    private final ProductRepository productRepository;

    public CategoryMetadataModel constructCategoryMetadata(ProductType productType) {
        float minPrice = productRepository.findMinPrice(productType).orElse(-1F);
        float maxPrice = productRepository.findMaxPrice(productType).orElse(-1F);

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        float[] popularSizes = queryPopularSizes(builder, productType.getMoonlightBit(), POPULAR_SIZES_LIMIT);
        Arrays.sort(popularSizes);

        return new CategoryMetadataModel(productType, new FloatRangeModel(minPrice, maxPrice), popularSizes);
    }

    public Page<CatalogItemModel> findItems(
            ProductType productType,
            CatalogFiltersDto filtersDto,
            CatalogSorting sorting,
            int pageNumber,
            String userEmail
    ) throws ApiException {
        List<Specification<Product>> activeFilters = resolveActiveFilters(productType, filtersDto);
        Specification<Product> specs = Specification.allOf(activeFilters);

        PageRequest pageRequest = PageRequest.of(pageNumber - 1, ITEMS_PER_PAGE, sorting.toJpaSort());
        Page<Product> page = productRepository.findAll(specs, pageRequest);

        if (userEmail != null) {
            int[] pagedIds = page.get().mapToInt(Product::getId).toArray();
            Set<Integer> favoriteIds = favoritesService.keepOnlyFavoriteIds(userEmail, pagedIds);
            return page.map(product -> CatalogItemModel.from(product, favoriteIds::contains));
        } else {
            return page.map(product -> CatalogItemModel.from(product, null));
        }
    }

    public ProductModel getProduct(int productId) throws ApiException {
        return productRepository.findById(productId).map(ProductModel::from).orElseThrow(() -> new ApiException(
                "product_not_found",
                "A product with this ID isn't exist!"
        ));
    }

    private static List<Specification<Product>> resolveActiveFilters(ProductType productType, CatalogFiltersDto filtersDto) throws ApiException {
        List<Specification<Product>> activeFilters = new ArrayList<>();
        activeFilters.add(ProductSpecs.hasType(productType));

        if (filtersDto.minPrice() != null || filtersDto.maxPrice() != null)
            activeFilters.add(ProductSpecs.hasPrice(filtersDto.minPrice(), filtersDto.maxPrice()));

        if (filtersDto.sizes() != null)
            activeFilters.add(ProductSpecs.hasSizes(parseSizes(filtersDto.sizes().split(","))));

        if (filtersDto.audiences() != null)
            activeFilters.add(ProductSpecs.hasAudiences(filtersDto.audiences()));

        if (filtersDto.materials() != null)
            activeFilters.add(ProductSpecs.hasMaterials(filtersDto.materials()));

        if (filtersDto.treasures() != null)
            activeFilters.add(ProductSpecs.hasTreasures(filtersDto.treasures()));

        return activeFilters;
    }

    private float[] queryPopularSizes(CriteriaBuilder builder, int productTypeBit, int limit) {
        CriteriaQuery<Float> query = builder.createQuery(Float.class);

        Root<ProductSizeMapping> root = query.from(ProductSizeMapping.class);
        Join<ProductSizeMapping, ProductSize> join = root.join(ProductSizeMapping_.productSize, JoinType.INNER);

        query.select(join.get(ProductSize_.size));

        query.where(ProductSpecs.asBitwiseAnd(builder, join.get(ProductSize_.productTypes), productTypeBit));
        query.groupBy(join.get(ProductSize_.size));
        query.orderBy(builder.desc(builder.count(root)));

        List<Float> resultList = entityManager.createQuery(query).setMaxResults(limit).getResultList();

        float[] result = new float[resultList.size()];
        for (int i = 0; i < resultList.size(); i++)
            result[i] = resultList.get(i);

        return result;
    }

    private static Set<Float> parseSizes(String[] rawSizes) throws ApiException {
        if (rawSizes == null || rawSizes.length == 0)
            throw new ApiException(HttpStatus.BAD_REQUEST, "bad_sizes_filter", "No values provided!");

        Set<Float> sizes = new TreeSet<>();
        for (String rawSize : rawSizes) {
            try {
                sizes.add(Float.parseFloat(rawSize));
            } catch (NumberFormatException ex) {
                throw new ApiException(
                        HttpStatus.BAD_REQUEST,
                        "bad_sizes_filter",
                        "Unexpected size value: '%s'".formatted(rawSize)
                );
            }
        }

        return sizes;
    }

}
