package ru.moonlightapp.backend.storage.repository.content;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.moonlightapp.backend.storage.model.content.FavoriteProduct;

public interface FavoriteProductRepository extends JpaRepository<FavoriteProduct, Long> {

}
