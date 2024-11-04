package ru.moonlightapp.backend.storage.model.content;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.moonlightapp.backend.storage.model.User;

import java.time.Instant;
import java.util.Objects;

@Getter
@NoArgsConstructor
@Entity @Table(name = "product_orders")
public final class ProductOrder {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User owner;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private JewelProduct product;

    @Column(name = "size", length = 10)
    private String size;

    @Column(name = "count", nullable = false)
    private int count;

    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Instant updatedAt;

    public ProductOrder(User owner, JewelProduct product, String size, int count) {
        this.owner = owner;
        this.product = product;
        this.size = size;
        this.count = count;
        this.createdAt = Instant.now();
        this.updatedAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProductOrder that = (ProductOrder) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ProductOrder{" +
                "id=" + id +
                ", owner=" + owner +
                ", product=" + product +
                ", size='" + size + '\'' +
                ", count=" + count +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

}
