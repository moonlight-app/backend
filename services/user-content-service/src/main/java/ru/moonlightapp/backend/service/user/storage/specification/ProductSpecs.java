package ru.moonlightapp.backend.service.user.storage.specification;

import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import ru.moonlightapp.backend.core.model.attribute.ProductType;
import ru.moonlightapp.backend.service.user.storage.model.Product;
import ru.moonlightapp.backend.service.user.storage.model.ProductSize;
import ru.moonlightapp.backend.service.user.storage.model.ProductSize_;
import ru.moonlightapp.backend.service.user.storage.model.Product_;

import java.util.Collection;

public final class ProductSpecs {

    public static Specification<Product> hasType(ProductType type) {
        return (root, query, builder) -> builder.equal(root.get(Product_.type), type);
    }

    public static Specification<Product> hasPrice(Float minPrice, Float maxPrice) {
        if (minPrice == null && maxPrice == null)
            throw new IllegalArgumentException("Both bounds cannot be null!");

        if (minPrice != null && maxPrice != null) {
            // minPrice <= price <= maxPrice
            return (root, query, builder) -> builder.between(root.get(Product_.price), minPrice, maxPrice);
        } else if (minPrice != null) {
            // minPrice <= price
            return (root, query, builder) -> builder.ge(root.get(Product_.price), minPrice);
        } else {
            // price <= maxPrice
            return (root, query, builder) -> builder.le(root.get(Product_.price), maxPrice);
        }
    }

    public static Specification<Product> hasSizes(Collection<Float> sizes) {
        return (root, query, builder) -> {
            if (query != null)
                query.distinct(true);

            Join<Product, ProductSize> productSizeJoin = root.join(Product_.productSizes, JoinType.INNER);
            return productSizeJoin.get(ProductSize_.size).in(sizes);
        };
    }

    public static Specification<Product> hasAudiences(int audiences) {
        return (root, query, builder) -> builder.or(
                builder.isNull(root.get(Product_.audiences)),
                asBitwiseAnd(builder, root.get(Product_.audiences), audiences)
        );
    }

    public static Specification<Product> hasMaterials(int materials) {
        return (root, query, builder) -> asBitwiseAnd(builder, root.get(Product_.materials), materials);
    }

    public static Specification<Product> hasTreasures(int treasures) {
        return (root, query, builder) -> asBitwiseAnd(builder, root.get(Product_.treasures), treasures);
    }

    public static Predicate asBitwiseAnd(CriteriaBuilder builder, Path<Integer> attributePath, int mask) {
        return builder.notEqual(builder.function("BITAND", Integer.class, attributePath, builder.literal(mask)), 0);
    }

}
