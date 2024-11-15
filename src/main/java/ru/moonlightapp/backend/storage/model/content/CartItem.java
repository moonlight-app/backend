package ru.moonlightapp.backend.storage.model.content;

import jakarta.persistence.*;
import lombok.*;
import ru.moonlightapp.backend.storage.model.User;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Getter
@Builder(setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity @Table(name = "cart_items")
public final class CartItem {

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

    public CartItem(String userEmail, int productId, String size, int count) {
        this.userEmail = userEmail;
        this.productId = productId;
        this.size = size;
        this.count = count;
        this.createdAt = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        this.updatedAt = createdAt;
    }

    public boolean updateCount(int count) {
        if (count == this.count)
            return false;

        this.count = count;
        onDataUpdated();
        return true;
    }

    private void onDataUpdated() {
        this.updatedAt = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CartItem cartItem = (CartItem) o;
        return id == cartItem.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "id=" + id +
                ", userEmail='" + userEmail + '\'' +
                ", productId=" + productId +
                ", size='" + size + '\'' +
                ", count=" + count +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

}
