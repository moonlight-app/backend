package ru.moonlightapp.backend.storage.repository.content;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.moonlightapp.backend.storage.model.content.CartItem;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    boolean existsByIdAndUserEmail(long id, String userEmail);

    Optional<CartItem> findByIdAndUserEmail(long id, String userEmail);

}
