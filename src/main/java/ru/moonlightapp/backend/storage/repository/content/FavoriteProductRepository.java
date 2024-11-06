package ru.moonlightapp.backend.storage.repository.content;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.moonlightapp.backend.storage.model.content.FavoriteProduct;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface FavoriteProductRepository extends JpaRepository<FavoriteProduct, Long> {

    boolean existsByOwnerEmailAndProductId(String ownerEmail, long productId);

    Optional<FavoriteProduct> findByOwnerEmailAndProductId(String ownerEmail, long productId);

    @Query("select p.product.id from FavoriteProduct p where p.owner.email = ?1 and p.product.id in ?2")
    Set<Long> keepOnlyFavoriteProductIds(String userEmail, Collection<Long> productIds);

}
