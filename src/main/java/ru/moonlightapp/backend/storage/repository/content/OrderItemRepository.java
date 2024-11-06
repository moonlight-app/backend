package ru.moonlightapp.backend.storage.repository.content;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.moonlightapp.backend.storage.model.content.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

}
