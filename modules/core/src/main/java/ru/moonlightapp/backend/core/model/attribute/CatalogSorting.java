package ru.moonlightapp.backend.core.model.attribute;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Sort;
import ru.moonlightapp.backend.core.protobuf.model.attribute.ProtoCatalogSorting;
import ru.moonlightapp.backend.core.util.KeyedEnumConstantFinder;

import java.util.Optional;

import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static ru.moonlightapp.backend.core.protobuf.model.attribute.ProtoCatalogSorting.*;

@Getter
@AllArgsConstructor
public enum CatalogSorting implements KeyedEnum {

    POPULARITY  ("popularity"),
    PRICE_ASC   ("price_asc"),
    PRICE_DESC  ("price_desc"),
    ;

    @JsonValue
    private final String key;

    public Sort toJpaSort() {
        return switch (this) {
            case POPULARITY -> Sort.by(ASC, "id");
            case PRICE_ASC -> Sort.by(ASC, "price").and(Sort.by(ASC, "id"));
            case PRICE_DESC -> Sort.by(DESC, "price").and(Sort.by(ASC, "id"));
        };
    }

    public static Optional<CatalogSorting> findByKey(String key) {
        return KeyedEnumConstantFinder.findByKey(key, values());
    }

    public static Optional<CatalogSorting> fromProto(ProtoCatalogSorting catalogSorting) {
        if (catalogSorting == null)
            return Optional.empty();

        return Optional.ofNullable(switch (catalogSorting) {
            case CATALOG_SORTING_POPULARITY -> POPULARITY;
            case CATALOG_SORTING_PRICE_ASC -> PRICE_ASC;
            case CATALOG_SORTING_PRICE_DESC -> PRICE_DESC;
            default -> null;
        });
    }

    public static ProtoCatalogSorting toProto(CatalogSorting catalogSorting) {
        if (catalogSorting == null)
            return CATALOG_SORTING_UNKNOWN;

        return catalogSorting.toProto();
    }

    public ProtoCatalogSorting toProto() {
        return switch (this) {
            case POPULARITY -> CATALOG_SORTING_POPULARITY;
            case PRICE_ASC -> CATALOG_SORTING_PRICE_ASC;
            case PRICE_DESC -> CATALOG_SORTING_PRICE_DESC;
        };
    }

}
