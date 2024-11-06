package ru.moonlightapp.backend.model.attribute;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Sort;
import ru.moonlightapp.backend.util.KeyedEnumConstantFinder;

import java.util.Optional;

import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

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

}
