package ru.moonlightapp.backend.storage.model.catalog;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.moonlightapp.backend.model.attribute.ProductType;

@Getter
@NoArgsConstructor
@Entity @Table(name = "catalog_items")
public final class CatalogItem {

    @Id @GeneratedValue
    @Column(name = "id", nullable = false)
    private long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "model", nullable = false)
    private String model;

    @Column(name = "price", nullable = false)
    private float price;

    @Column(name = "type", nullable = false)
    private ProductType type;

    @Column(name = "sizes")
    private String sizes;

    @Column(name = "materials")
    private String materials;

    @Column(name = "audiences")
    private String audiences;

    @Column(name = "treasures")
    private String treasures;

    @Column(name = "preview_url")
    private String previewUrl;

    @Override
    public String toString() {
        return "CatalogItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", model='" + model + '\'' +
                ", price=" + price +
                ", type=" + type +
                ", sizes='" + sizes + '\'' +
                ", materials='" + materials + '\'' +
                ", audiences='" + audiences + '\'' +
                ", treasures='" + treasures + '\'' +
                ", previewUrl='" + previewUrl + '\'' +
                '}';
    }

}
