package ru.moonlightapp.backend.storage.repository.content;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import ru.moonlightapp.backend.model.attribute.ProductType;
import ru.moonlightapp.backend.storage.model.content.Product;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer>, JpaSpecificationExecutor<Product> {

    @Query("select min(p.price) from Product p where p.type = ?1")
    Optional<Float> findMinPrice(ProductType productType);

    @Query("select max(p.price) from Product p where p.type = ?1")
    Optional<Float> findMaxPrice(ProductType productType);

}
