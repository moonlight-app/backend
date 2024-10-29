package ru.moonlightapp.backend.storage.repository.catalog;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.moonlightapp.backend.storage.model.catalog.CatalogItem;

public interface CatalogItemRepository extends JpaRepository<CatalogItem, Long> {

}
