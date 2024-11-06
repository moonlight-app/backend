package ru.moonlightapp.backend.storage.repository.content;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.moonlightapp.backend.storage.model.content.FavoriteItem;

import java.util.Optional;
import java.util.Set;

public interface FavoriteItemRepository extends JpaRepository<FavoriteItem, Long> {

    boolean existsByOwnerEmailAndProductId(String ownerEmail, long productId);

    Optional<FavoriteItem> findByOwnerEmailAndProductId(String ownerEmail, long productId);

    @Query("select i.product.id from FavoriteItem i where i.owner.email = ?1 and i.product.id in ?2")
    Set<Integer> keepOnlyFavoriteProductIds(String userEmail, int[] productIds);

}
