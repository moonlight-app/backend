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
    PLATINUM    ("platinum",    3),
    WHITE_GOLD  ("white_gold",  4),
    PINK_GOLD   ("pink_gold",   5),
    CERAMICS    ("ceramics",    6),
    ;

    @JsonValue
    private final String key;
    private final int moonlightId;

    public static Optional<Material> findByKey(String key) {
        return KeyedEnumConstantFinder.findByKey(key, values());
    }

}
