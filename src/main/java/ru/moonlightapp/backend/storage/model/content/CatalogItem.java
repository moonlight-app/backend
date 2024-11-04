package ru.moonlightapp.backend.storage.model.content;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.moonlightapp.backend.model.attribute.ProductType;

import java.util.Objects;

@Getter
@NoArgsConstructor
@Entity @Table(name = "jewel_products")
public final class CatalogItem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "type", nullable = false, length = 8)
    private ProductType type;

    @Column(name = "name", nullable = false, length = 0)
    private String name;

    @Column(name = "price", nullable = false)
    private float price;

    @Column(name = "preview_url", length = 0)
    private String previewUrl;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CatalogItem that = (CatalogItem) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "CatalogItem{" +
                "id=" + id +
                ", type=" + type +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", previewUrl='" + previewUrl + '\'' +
                '}';
    }

}
