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
import ru.moonlightapp.backend.service.user.api.model.CartItemModel;
import ru.moonlightapp.backend.service.user.storage.model.CartItem;
import ru.moonlightapp.backend.service.user.storage.model.CartItem_;
import ru.moonlightapp.backend.service.user.storage.model.Product;
import ru.moonlightapp.backend.service.user.storage.model.Product_;
import ru.moonlightapp.backend.service.user.storage.projection.CartItemProj;
import ru.moonlightapp.backend.service.user.storage.repository.CartItemRepository;
import ru.moonlightapp.backend.service.user.storage.repository.ProductRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public final class CartService {

    private static final int ITEMS_PER_PAGE = 20;

    private final EntityManager entityManager;
    private final FavoritesService favoritesService;

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartItemModel addItem(String userEmail, int productId, String size, int count) throws ApiException {
        Optional<Product> product = productRepository.findById(productId);
        if (product.isEmpty())
            throw new ApiException("product_not_found", "A product with this ID isn't exist!");

        if (product.get().isSized()) {
            if (size == null || size.isEmpty()) {
                throw new ApiException("product_size_required", "This product requires a certain size to add to cart!");
            }

            if (!product.get().hasSize(size)) {
                throw new ApiException("product_size_not_found", "This product hasn't this size!");
            }
        }

        CartItem item = cartItemRepository.save(new CartItem(userEmail, productId, size, count));
        return CartItemModel.from(item, product.get(), id -> favoritesService.isFavorited(userEmail, id));
    }

    public void changeCount(String userEmail, long itemId, int count) throws ApiException {
        CartItem cartItem = cartItemRepository.findByIdAndUserEmail(itemId, userEmail).orElseThrow(() -> new ApiException(
                "cart_item_not_found",
                "A cart item not found!"
        ));

        if (cartItem.updateCount(count)) {
            cartItemRepository.save(cartItem);
        }
    }

    public Page<CartItemModel> findItems(String userEmail, int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber - 1, ITEMS_PER_PAGE);

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        List<CartItemProj> content = queryItems(builder, userEmail, pageable);
        long count = countItems(builder, userEmail);

        Page<CartItemProj> page = new PageImpl<>(content, pageable, count);
        int[] pagedIds = page.get().mapToInt(CartItemProj::productId).toArray();
        Set<Integer> favoriteIds = favoritesService.keepOnlyFavoriteIds(userEmail, pagedIds);
        return page.map(proj -> CartItemModel.from(proj, favoriteIds::contains));
    }

    public void removeItem(String userEmail, long itemId) throws ApiException {
        if (!cartItemRepository.existsByIdAndUserEmail(itemId, userEmail))
            throw new ApiException("cart_item_not_found", "A cart item not found!");

        cartItemRepository.deleteById(itemId);
    }

    public void removeItems(Iterable<Long> ids) {
        cartItemRepository.deleteAllById(ids);
    }

    private List<CartItemProj> queryItems(CriteriaBuilder builder, String userEmail, Pageable pageable) {
        CriteriaQuery<CartItemProj> query = builder.createQuery(CartItemProj.class);

        Root<Product> root = query.from(Product.class);
        Join<Product, CartItem> join = root.join(Product_.cartItems, JoinType.INNER);

        query.select(builder.construct(CartItemProj.class,
                join.get(CartItem_.id),
                root.get(Product_.id),
                root.get(Product_.type),
                root.get(Product_.name),
                root.get(Product_.price),
                join.get(CartItem_.size),
                join.get(CartItem_.count),
                root.get(Product_.previewUrl),
                join.get(CartItem_.createdAt))
        );

        query.where(builder.equal(join.get(CartItem_.userEmail), userEmail));
        query.orderBy(builder.desc(join.get(CartItem_.createdAt)));

        return entityManager.createQuery(query)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
    }

    private long countItems(CriteriaBuilder builder, String userEmail) {
        CriteriaQuery<Long> query = builder.createQuery(Long.class);

        Root<Product> root = query.from(Product.class);
        Join<Product, CartItem> join = root.join(Product_.cartItems, JoinType.INNER);

        query.select(builder.count(root));
        query.where(builder.equal(join.get(CartItem_.userEmail), userEmail));

        return entityManager.createQuery(query).getSingleResult();
    }

    List<CartItemProj> queryOwnedItems(CriteriaBuilder builder, String userEmail, Set<Long> ids) {
        CriteriaQuery<CartItemProj> query = builder.createQuery(CartItemProj.class);

        Root<CartItem> root = query.from(CartItem.class);
        Join<CartItem, Product> join = root.join(CartItem_.product, JoinType.INNER);

        query.select(builder.construct(CartItemProj.class,
                root.get(CartItem_.id),
                join.get(Product_.id),
                join.get(Product_.type),
                join.get(Product_.name),
                join.get(Product_.price),
                root.get(CartItem_.size),
                root.get(CartItem_.count),
                join.get(Product_.previewUrl),
                root.get(CartItem_.createdAt))
        );

        query.where(builder.and(
                builder.equal(root.get(CartItem_.userEmail), userEmail),
                root.get(CartItem_.id).in(ids)
        ));

        return entityManager.createQuery(query).getResultList();
    }

}
