package ru.moonlightapp.backend.service.user.storage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import ru.moonlightapp.backend.core.model.attribute.ProductType;
import ru.moonlightapp.backend.service.user.storage.model.Product;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer>, JpaSpecificationExecutor<Product> {

    @Query("select min(p.price) from Product p where p.type = ?1")
    Optional<Float> findMinPrice(ProductType productType);

    @Query("select max(p.price) from Product p where p.type = ?1")
    Optional<Float> findMaxPrice(ProductType productType);

}
