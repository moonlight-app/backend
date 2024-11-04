package ru.moonlightapp.backend.storage.repository.content;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.moonlightapp.backend.storage.model.content.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {



}
