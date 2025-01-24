package ru.moonlightapp.backend.service.user.storage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.moonlightapp.backend.service.user.storage.model.CartItem;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    boolean existsByIdAndUserEmail(long id, String userEmail);

    Optional<CartItem> findByIdAndUserEmail(long id, String userEmail);

}
