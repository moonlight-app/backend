package ru.moonlightapp.backend.service.user.storage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.moonlightapp.backend.service.user.storage.model.OrderItem;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query(value = """
            select * from order_items i
            where
                i.status = 0 and
                extract(epoch from (now() - i.created_at)) > ?1
            """, nativeQuery = true)
    List<OrderItem> findCloseableOrders(long elapsedSecondsThreshold);

}
