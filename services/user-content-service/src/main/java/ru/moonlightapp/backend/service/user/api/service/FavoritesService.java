package ru.moonlightapp.backend.service.user.api.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.moonlightapp.backend.core.exception.ApiException;
import ru.moonlightapp.backend.service.user.api.model.FavoriteItemModel;
import ru.moonlightapp.backend.service.user.storage.model.FavoriteItem;
import ru.moonlightapp.backend.service.user.storage.model.FavoriteItem_;
import ru.moonlightapp.backend.service.user.storage.model.Product;
import ru.moonlightapp.backend.service.user.storage.model.Product_;
import ru.moonlightapp.backend.service.user.storage.projection.FavoriteItemProj;
import ru.moonlightapp.backend.service.user.storage.repository.FavoriteItemRepository;
import ru.moonlightapp.backend.service.user.storage.repository.ProductRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public final class FavoritesService {

    private static final int ITEMS_PER_PAGE = 20;

    private final EntityManager entityManager;

    private final FavoriteItemRepository favoriteItemRepository;
    private final ProductRepository productRepository;

    public FavoriteItemModel addItem(String userEmail, int productId) throws ApiException {
        Optional<Product> product = productRepository.findById(productId);
        if (product.isEmpty())
            throw new ApiException("product_not_found", "A product with this ID isn't exist!");

        if (isFavorited(userEmail, productId))
            throw new ApiException("favorite_item_already_exists", "This product is already favorited by this user!");

        FavoriteItem item = favoriteItemRepository.save(new FavoriteItem(userEmail, productId));
        return FavoriteItemModel.from(item, product.get());
    }

    public Page<FavoriteItemModel> findItems(String userEmail, int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber - 1, ITEMS_PER_PAGE);

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        List<FavoriteItemProj> content = queryItems(builder, userEmail, pageable);
        long count = countItems(builder, userEmail);

        Page<FavoriteItemProj> page = new PageImpl<>(content, pageable, count);
        return page.map(FavoriteItemModel::from);
    }

    public boolean isFavorited(String userEmail, int productId) {
        return favoriteItemRepository.existsByUserEmailAndProductId(userEmail, productId);
    }

    public Set<Integer> keepOnlyFavoriteIds(String userEmail, int[] ids) {
        return favoriteItemRepository.keepOnlyFavoriteProductIds(userEmail, ids);
    }

    public void removeItem(String userEmail, long itemId) throws ApiException {
        if (!favoriteItemRepository.existsByIdAndUserEmail(itemId, userEmail))
            throw new ApiException("favorite_item_not_found", "A favorite item not found!");

        favoriteItemRepository.deleteById(itemId);
    }

    private List<FavoriteItemProj> queryItems(CriteriaBuilder builder, String userEmail, Pageable pageable) {
        CriteriaQuery<FavoriteItemProj> query = builder.createQuery(FavoriteItemProj.class);

        Root<Product> root = query.from(Product.class);
        Join<Product, FavoriteItem> join = root.join(Product_.favoriteItems, JoinType.INNER);

        query.select(builder.construct(FavoriteItemProj.class,
                join.get(FavoriteItem_.id),
                root.get(Product_.id),
                root.get(Product_.type),
                root.get(Product_.name),
                root.get(Product_.price),
                root.get(Product_.previewUrl),
                join.get(FavoriteItem_.createdAt))
        );

        query.where(builder.equal(join.get(FavoriteItem_.userEmail), userEmail));
        query.orderBy(builder.desc(join.get(FavoriteItem_.createdAt)));

        return entityManager.createQuery(query)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
    }

    private long countItems(CriteriaBuilder builder, String userEmail) {
        CriteriaQuery<Long> query = builder.createQuery(Long.class);

        Root<Product> root = query.from(Product.class);
        Join<Product, FavoriteItem> join = root.join(Product_.favoriteItems, JoinType.INNER);

        query.select(builder.count(root));
        query.where(builder.equal(join.get(FavoriteItem_.userEmail), userEmail));

        return entityManager.createQuery(query).getSingleResult();
    }

}
