package ru.moonlightapp.backend.service.user.storage.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.Set;

@Getter
@NoArgsConstructor
@Entity @Table(name = "product_sizes")
public final class ProductSize {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "size", nullable = false)
    private float size;

    @Column(name = "product_types", nullable = false)
    private int productTypes;

    @ManyToMany(mappedBy = "productSizes", fetch = FetchType.LAZY)
    private Set<Product> products;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProductSize that = (ProductSize) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ProductSize{" +
                "id=" + id +
                ", size=" + size +
                ", productTypes=" + productTypes +
                '}';
    }

}
