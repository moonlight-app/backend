package ru.moonlightapp.backend.storage.model.catalog;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.moonlightapp.backend.model.attribute.Audience;
import ru.moonlightapp.backend.model.attribute.Material;
import ru.moonlightapp.backend.model.attribute.ProductType;
import ru.moonlightapp.backend.model.attribute.Treasure;

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

    @Column()
    private ProductType type;

    private float[] size;

    private Material[] materials;

    private Audience[] audiences;

    @Column(name = "treasures")
    @Convert(converter = )
    private Treasure[] treasures;

    @Column(name = "preview_url")
    private String previewUrl;

}
