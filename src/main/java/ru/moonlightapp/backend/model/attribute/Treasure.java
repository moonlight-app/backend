package ru.moonlightapp.backend.model.attribute;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.moonlightapp.backend.util.KeyedEnumConstantFinder;

import java.util.Optional;

@Getter
@AllArgsConstructor
public enum Treasure implements KeyedEnum {

    NOTHING     ("nothing"),
    DIAMOND     ("diamond"),
    SAPPHIRE    ("sapphire"),
    PEARL       ("pearl"),
    AMETHYST    ("amethyst"),
    FIANIT      ("fianit"),
    EMERALD     ("emerald"),
    RUBY        ("ruby"),
    ;

    @JsonValue
    private final String key;

    public static Optional<Treasure> findByKey(String key) {
        return KeyedEnumConstantFinder.findByKey(key, values());
    }

}
