package ru.moonlightapp.backend.service.user.storage.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.moonlightapp.backend.core.model.attribute.OrderStatus;
import ru.moonlightapp.backend.core.storage.model.User;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Getter
@NoArgsConstructor
@Entity @Table(name = "order_items")
public final class OrderItem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @Column(name = "owner_email", nullable = false)
    private String userEmail;

    @Column(name = "product_id", nullable = false)
    private int productId;

    @Column(name = "size", length = 10)
    private String size;

    @Column(name = "count", nullable = false)
    private int count;

    @Column(name = "status", nullable = false, length = 11)
    private OrderStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_email", insertable = false, updatable = false)
    private User owner;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;

    public OrderItem(String userEmail, int productId, String size, int count) {
        this.userEmail = userEmail;
        this.productId = productId;
        this.size = size;
        this.count = count;
        this.status = OrderStatus.IN_DELIVERY;
        this.createdAt = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        this.updatedAt = createdAt;
    }

    public void closeOrder() {
        this.status = OrderStatus.CLOSED;
        onDataUpdated();
    }

    private void onDataUpdated() {
        this.updatedAt = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrderItem that = (OrderItem) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "id=" + id +
                ", userEmail='" + userEmail + '\'' +
                ", productId=" + productId +
                ", size='" + size + '\'' +
                ", count=" + count +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

}
