package ru.moonlightapp.backend.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.moonlightapp.backend.storage.model.content.Product;

import java.util.function.Function;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CatalogItemModel(
        @JsonProperty("id") int id,
        @JsonProperty("name") String name,
        @JsonProperty("price") float price,
        @JsonProperty("preview_url") String previewUrl,
        @JsonProperty("is_favorite") Boolean isFavorite
) {

    public static Builder builder() {
        return new Builder();
    }

    public static CatalogItemModel from(Product product) {
        return from(product, null);
    }

    public static CatalogItemModel from(Product product, Function<Integer, Boolean> favoriteStateResolver) {
        return builder()
                .withId(product.getId())
                .withName(product.getName())
                .withPrice(product.getPrice())
                .withPreviewUrl(product.getPreviewUrl())
                .withIsFavorite(favoriteStateResolver != null ? favoriteStateResolver.apply(product.getId()) : null)
                .build();
    }

    public static final class Builder {

        private int id;
        private String name;
        private float price;
        private String previewUrl;
        private Boolean isFavorite;

        public CatalogItemModel build() {
            return new CatalogItemModel(id, name, price, previewUrl, isFavorite);
        }

        public Builder withId(int id) {
            this.id = id;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withPrice(float price) {
            this.price = price;
            return this;
        }

        public Builder withPreviewUrl(String previewUrl) {
            this.previewUrl = previewUrl;
            return this;
        }

        public Builder withIsFavorite(Boolean isFavorite) {
            this.isFavorite = isFavorite;
            return this;
        }

    }

}
