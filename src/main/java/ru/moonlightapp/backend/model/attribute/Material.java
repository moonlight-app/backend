package ru.moonlightapp.backend.model.attribute;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.moonlightapp.backend.util.KeyedEnumConstantFinder;

import java.util.Optional;

@Getter
@AllArgsConstructor
public enum Material implements KeyedEnum {

    GOLD        ("gold",        1),
    SILVER      ("silver",      2),
    PLATINUM    ("platinum",    4),
    WHITE_GOLD  ("white_gold",  8),
    PINK_GOLD   ("pink_gold",   16),
    CERAMICS    ("ceramics",    32),
    ;

    @JsonValue
    private final String key;
    private final int moonlightBit;

    public static Optional<Material> findByKey(String key) {
        return KeyedEnumConstantFinder.findByKey(key, values());
    }

}
