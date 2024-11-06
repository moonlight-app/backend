package ru.moonlightapp.backend.storage.repository.content;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.moonlightapp.backend.storage.model.content.FavoriteProduct;

import java.util.Set;

public interface FavoriteProductRepository extends JpaRepository<FavoriteProduct, Long> {

    @Query("select p.product.id from FavoriteProduct p where p.owner.email = ?1")
    Set<Long> findFavoriteProductIds(String userEmail);

}
