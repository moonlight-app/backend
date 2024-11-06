package ru.moonlightapp.backend.api.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.moonlightapp.backend.api.model.CartItemModel;
import ru.moonlightapp.backend.exception.ApiException;
import ru.moonlightapp.backend.storage.model.User;
import ru.moonlightapp.backend.storage.model.User_;
import ru.moonlightapp.backend.storage.model.content.CartItem;
import ru.moonlightapp.backend.storage.model.content.CartItem_;
import ru.moonlightapp.backend.storage.model.content.Product;
import ru.moonlightapp.backend.storage.model.content.Product_;
import ru.moonlightapp.backend.storage.projection.CartItemProj;
import ru.moonlightapp.backend.storage.repository.content.CartItemRepository;
import ru.moonlightapp.backend.storage.repository.content.ProductRepository;

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

    public CartItemModel addItem(User owner, int productId, String size, int count) throws ApiException {
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

        CartItem item = cartItemRepository.save(new CartItem(owner, product.get(), size, count));
        return CartItemModel.from(item, product.get(), id -> favoritesService.isFavorited(owner.getEmail(), id));
    }

    public Page<CartItemModel> findItems(String ownerEmail, int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber - 1, ITEMS_PER_PAGE);

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        List<CartItemProj> content = queryItems(builder, ownerEmail, pageable);
        long count = countItems(builder, ownerEmail);

        Page<CartItemProj> page = new PageImpl<>(content, pageable, count);
        int[] pagedIds = page.get().mapToInt(CartItemProj::productId).toArray();
        Set<Integer> favoriteIds = favoritesService.keepOnlyFavoriteIds(ownerEmail, pagedIds);
        return page.map(proj -> CartItemModel.from(proj, favoriteIds::contains));
    }

    public void removeItem(String ownerEmail, long itemId) throws ApiException {
        Optional<CartItem> existing = cartItemRepository.findById(itemId);
        if (existing.isEmpty() || !ownerEmail.equals(existing.map(CartItem::getOwner).map(User::getEmail).orElse(null)))
            throw new ApiException("cart_item_not_found", "A cart item not found!");

        cartItemRepository.deleteById(itemId);
    }

    private List<CartItemProj> queryItems(CriteriaBuilder builder, String ownerEmail, Pageable pageable) {
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

        query.where(builder.equal(join.get(CartItem_.owner).get(User_.email), ownerEmail));
        query.orderBy(builder.desc(join.get(CartItem_.createdAt)));

        return entityManager.createQuery(query)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
    }

    private long countItems(CriteriaBuilder builder, String ownerEmail) {
        CriteriaQuery<Long> query = builder.createQuery(Long.class);

        Root<Product> root = query.from(Product.class);
        Join<Product, CartItem> join = root.join(Product_.cartItems, JoinType.INNER);

        query.select(builder.count(root));
        query.where(builder.equal(join.get(CartItem_.owner).get(User_.email), ownerEmail));

        return entityManager.createQuery(query).getSingleResult();
    }

}
