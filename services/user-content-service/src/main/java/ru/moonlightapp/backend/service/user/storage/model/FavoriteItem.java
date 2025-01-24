package ru.moonlightapp.backend.service.user.storage.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.moonlightapp.backend.core.storage.model.User;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Getter
@NoArgsConstructor
@Entity @Table(name = "favorite_items")
public final class FavoriteItem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @Column(name = "owner_email", nullable = false)
    private String userEmail;

    @Column(name = "product_id", nullable = false)
    private int productId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_email", insertable = false, updatable = false)
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;

    public FavoriteItem(String userEmail, int productId) {
        this.userEmail = userEmail;
        this.productId = productId;
        this.createdAt = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        this.updatedAt = createdAt;
    }

    private void onDataUpdated() {
        this.updatedAt = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FavoriteItem that = (FavoriteItem) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "FavoriteItem{" +
                "id=" + id +
                ", userEmail='" + userEmail + '\'' +
                ", productId=" + productId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

}
