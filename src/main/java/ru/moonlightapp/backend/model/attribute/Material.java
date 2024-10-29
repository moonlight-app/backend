package ru.moonlightapp.backend.model.attribute;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.moonlightapp.backend.util.KeyedEnumConstantFinder;

import java.util.Optional;

@Getter
@AllArgsConstructor
public enum Material implements KeyedEnum {

    GOLD        ("gold"),
    SILVER      ("silver"),
    PLATINUM    ("platinum"),
    WHITE_GOLD  ("white_gold"),
    PINK_GOLD   ("pink_gold"),
    CERAMICS    ("ceramics"),
    ;

    @JsonValue
    private final String key;

    public static Optional<Material> findByKey(String key) {
        return KeyedEnumConstantFinder.findByKey(key, values());
    }

}
