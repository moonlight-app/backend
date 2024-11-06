package ru.moonlightapp.backend.storage.model.content;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.moonlightapp.backend.storage.model.User;

import java.time.Instant;
import java.util.Objects;

@Getter
@NoArgsConstructor
@Entity @Table(name = "favorite_items")
public final class FavoriteItem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public FavoriteItem(User owner, Product product) {
        this.owner = owner;
        this.product = product;
        this.createdAt = Instant.now();
        this.updatedAt = createdAt;
    }

    private void onDataUpdated() {
        this.updatedAt = Instant.now();
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
                ", owner=" + owner +
                ", product=" + product +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

}
