package ru.moonlightapp.backend.model.attribute;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.moonlightapp.backend.util.KeyedEnumConstantFinder;

import java.util.Optional;

@Getter
@AllArgsConstructor
public enum ProductType implements KeyedEnum {

    RING        ("ring"),
    BRACELET    ("bracelet"),
    CHAIN       ("chain"),
    WATCH       ("watch"),
    EARRINGS    ("earrings"),
    NECKLACE    ("necklace"),
    ;

    @JsonValue
    private final String key;

    public static Optional<ProductType> findByKey(String key) {
        return KeyedEnumConstantFinder.findByKey(key, values());
    }

}
