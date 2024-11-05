package ru.moonlightapp.backend.storage.repository.content;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.moonlightapp.backend.storage.model.User;
import ru.moonlightapp.backend.storage.model.content.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Page<CartItem> findAllByOwner(User owner, Pageable pageable);

}
