package ru.moonlightapp.backend.service.user.storage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.moonlightapp.backend.service.user.storage.model.ProductSize;

public interface ProductSizeRepository extends JpaRepository<ProductSize, Long> {

}
