package ru.moonlightapp.backend.storage.repository.content;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.moonlightapp.backend.storage.model.content.JewelProduct;

public interface JewelProductRepository extends JpaRepository<JewelProduct, Long> {

}
