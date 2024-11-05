package ru.moonlightapp.backend.model.attribute;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.moonlightapp.backend.util.KeyedEnumConstantFinder;

import java.util.Optional;

@Getter
@AllArgsConstructor
public enum ProductType implements KeyedEnum {

    RING        ("ring",        1),
    BRACELET    ("bracelet",    2),
    CHAIN       ("chain",       4),
    WATCH       ("watch",       8),
    EARRINGS    ("earrings",    16),
    NECKLACE    ("necklace",    32),
    ;

    @JsonValue
    private final String key;
    private final int moonlightBit;

    public static Optional<ProductType> findByKey(String key) {
        return KeyedEnumConstantFinder.findByKey(key, values());
    }

}
