package ru.moonlightapp.backend.model.attribute;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.moonlightapp.backend.util.KeyedEnumConstantFinder;

import java.util.Optional;

@Getter
@AllArgsConstructor
public enum Audience implements KeyedEnum {

    MEN         ("men",         1),
    WOMEN       ("women",       2),
    CHILDREN    ("children",    4),
    UNISEX      ("unisex",      8),
    ;

    @JsonValue
    private final String key;
    private final int moonlightBit;

    public static Optional<Audience> findByKey(String key) {
        return KeyedEnumConstantFinder.findByKey(key, values());
    }

}
