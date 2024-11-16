package ru.moonlightapp.backend.storage.repository.content;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.moonlightapp.backend.storage.model.content.ProductSize;

public interface ProductSizeRepository extends JpaRepository<ProductSize, Long> {

}
