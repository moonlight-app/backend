package ru.moonlightapp.backend.storage.model.content;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.moonlightapp.backend.model.attribute.ProductType;

import java.util.Objects;
import java.util.Set;

@Getter
@NoArgsConstructor
@Entity @Table(name = "products")
public final class Product {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "article", nullable = false, unique = true)
    private int article;

    @Column(name = "type", nullable = false, length = 8)
    private ProductType type;

    @Column(name = "name", nullable = false, length = 0)
    private String name;

    @Column(name = "price", nullable = false)
    private float price;

    @Column(name = "sizes", length = 0)
    private String sizes;

    @Column(name = "audiences")
    private Integer audiences;

    @Column(name = "materials", nullable = false)
    private int materials;

    @Column(name = "sample", length = 10)
    private String sample;

    @Column(name = "sample_type", length = 30)
    private String sampleType;

    @Column(name = "treasures", nullable = false)
    private int treasures;

    @Column(name = "weight")
    private Float weight;

    @Column(name = "preview_url", length = 0, nullable = false)
    private String previewUrl;

    @Column(name = "description", length = 0)
    private String description;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "product_size_mappings",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "product_size_id")
    )
    private Set<ProductSize> productSizes;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Set<FavoriteItem> favoriteItems;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Set<OrderItem> orderItems;

    public String[] getSizesAsArray() {
        if (!isSized())
            return null;

        String[] sizes = this.sizes.split(",");
        return sizes.length != 0 ? sizes : null;
    }

    public boolean isSized() {
        return sizes != null && !sizes.isEmpty();
    }

    public boolean hasSize(String size) {
        String[] sizes = getSizesAsArray();
        if (sizes != null)
            for (String existing : sizes)
                if (size.equals(existing))
                    return true;

        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Product that = (Product) o;
        return article == that.article;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(article);
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", article=" + article +
                ", type=" + type +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", sizes='" + sizes + '\'' +
                ", audiences=" + audiences +
                ", materials=" + materials +
                ", sample='" + sample + '\'' +
                ", sampleType='" + sampleType + '\'' +
                ", treasures=" + treasures +
                ", weight=" + weight +
                ", previewUrl='" + previewUrl + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

}
