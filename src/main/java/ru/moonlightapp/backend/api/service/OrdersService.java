package ru.moonlightapp.backend.api.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.moonlightapp.backend.api.model.OrderItemModel;
import ru.moonlightapp.backend.exception.ApiException;
import ru.moonlightapp.backend.storage.model.content.OrderItem;
import ru.moonlightapp.backend.storage.model.content.OrderItem_;
import ru.moonlightapp.backend.storage.model.content.Product;
import ru.moonlightapp.backend.storage.model.content.Product_;
import ru.moonlightapp.backend.storage.projection.CartItemProj;
import ru.moonlightapp.backend.storage.projection.OrderItemProj;
import ru.moonlightapp.backend.storage.repository.content.OrderItemRepository;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public final class OrdersService {

    private static final int MAX_ITEMS_PER_REQUEST = 10;
    private static final int ITEMS_PER_PAGE = 20;

    private final EntityManager entityManager;
    private final CartService cartService;

    private final OrderItemRepository orderItemRepository;

    public List<OrderItemModel> addItems(String userEmail, String rawCartItemIds) throws ApiException {
        Set<Long> ids = parseCartItemIds(rawCartItemIds);

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        List<CartItemProj> cartItemProjs = cartService.queryOwnedItems(builder, userEmail, ids);
        List<Long> foundIds = cartItemProjs.stream().map(CartItemProj::itemId).toList();

        if (foundIds.size() < ids.size()) {
            List<Long> notFoundIds = ids.stream().filter(Predicate.not(foundIds::contains)).toList();
            throw new ApiException("missing_data", "Some cart item(s) or product(s) not found!").withPayload(notFoundIds);
        }

        cartService.removeItems(foundIds);

        Map<Long, CartItemProj> mappings = cartItemProjs.stream().collect(Collectors.toMap(
                CartItemProj::itemId,
                Function.identity()
        ));

        Map<Long, OrderItem> orderItems = cartItemProjs.stream().collect(Collectors.toMap(
                CartItemProj::itemId,
                proj -> new OrderItem(userEmail, proj.productId(), proj.size(), proj.count())
        ));

        orderItemRepository.saveAll(orderItems.values());

        return orderItems.entrySet().stream()
                .map(entry -> OrderItemModel.from(entry.getValue(), mappings.get(entry.getKey())))
                .toList();
    }

    public Page<OrderItemModel> findItems(String userEmail, int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber - 1, ITEMS_PER_PAGE);

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        List<OrderItemProj> content = queryItems(builder, userEmail, pageable);
        long count = countItems(builder, userEmail);

        Page<OrderItemProj> page = new PageImpl<>(content, pageable, count);
        return page.map(OrderItemModel::from);
    }

    private List<OrderItemProj> queryItems(CriteriaBuilder builder, String userEmail, Pageable pageable) {
        CriteriaQuery<OrderItemProj> query = builder.createQuery(OrderItemProj.class);

        Root<Product> root = query.from(Product.class);
        Join<Product, OrderItem> join = root.join(Product_.orderItems, JoinType.INNER);

        query.select(builder.construct(OrderItemProj.class,
                join.get(OrderItem_.id),
                root.get(Product_.id),
                root.get(Product_.type),
                root.get(Product_.name),
                root.get(Product_.price),
                join.get(OrderItem_.size),
                join.get(OrderItem_.count),
                root.get(Product_.previewUrl),
                join.get(OrderItem_.status),
                join.get(OrderItem_.createdAt))
        );

        query.where(builder.equal(join.get(OrderItem_.userEmail), userEmail));
        query.orderBy(
                builder.asc(join.get(OrderItem_.status)),
                builder.desc(join.get(OrderItem_.createdAt))
        );

        return entityManager.createQuery(query)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
    }

    private long countItems(CriteriaBuilder builder, String userEmail) {
        CriteriaQuery<Long> query = builder.createQuery(Long.class);

        Root<Product> root = query.from(Product.class);
        Join<Product, OrderItem> join = root.join(Product_.orderItems, JoinType.INNER);

        query.select(builder.count(root));
        query.where(builder.equal(join.get(OrderItem_.userEmail), userEmail));

        return entityManager.createQuery(query).getSingleResult();
    }

    private static Set<Long> parseCartItemIds(String rawCartItemIds) throws ApiException {
        String[] rawIds = rawCartItemIds.split(",");
        if (rawIds.length > MAX_ITEMS_PER_REQUEST)
            throw new ApiException("too_much_cart_items", "Too much cart items to order!").withPayload(MAX_ITEMS_PER_REQUEST);

        Set<Long> ids = new LinkedHashSet<>();
        for (String rawId : rawIds) {
            try {
                ids.add(Long.parseLong(rawId));
            } catch (NumberFormatException ex) {
                throw new ApiException("bad_cart_items", "Cart items IDs parameter is malformed!").withPayload(rawId);
            }
        }

        return ids;
    }

}
