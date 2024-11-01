package ru.moonlightapp.backend.storage.model.content;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.moonlightapp.backend.model.attribute.ProductType;

import java.util.Objects;

@Getter
@NoArgsConstructor
@Entity @Table(name = "jewel_products")
public final class JewelProduct {

    @Id
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "article", nullable = false, unique = true)
    private long article;

    @Column(name = "type", nullable = false, length = 8)
    private ProductType type;

    @Column(name = "name", nullable = false, length = 0)
    private String name;

    @Column(name = "price", nullable = false)
    private float price;

    @Column(name = "sizes", length = 0)
    private String sizes;

    @Column(name = "audiences", length = 10)
    private String audiences;

    @Column(name = "materials", length = 15)
    private String materials;

    @Column(name = "sample", length = 10)
    private String sample;

    @Column(name = "sample_type", length = 30)
    private String sampleType;

    @Column(name = "treasures", length = 15)
    private String treasures;

    @Column(name = "weight")
    private Float weight;

    @Column(name = "preview_url", length = 0)
    private String previewUrl;

    @Column(name = "description", length = 0)
    private String description;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JewelProduct that = (JewelProduct) o;
        return article == that.article;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(article);
    }

    @Override
    public String toString() {
        return "JewelProduct{" +
                "id=" + id +
                ", article=" + article +
                ", type=" + type +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", sizes='" + sizes + '\'' +
                ", audiences='" + audiences + '\'' +
                ", materials='" + materials + '\'' +
                ", sample='" + sample + '\'' +
                ", sampleType='" + sampleType + '\'' +
                ", treasures='" + treasures + '\'' +
                ", weight=" + weight +
                ", previewUrl='" + previewUrl + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

}
