package ru.moonlightapp.backend.model.attribute;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.moonlightapp.backend.util.KeyedEnumConstantFinder;

import java.util.Optional;

@Getter
@AllArgsConstructor
public enum Treasure implements KeyedEnum {

    NOTHING     ("nothing",     1),
    DIAMOND     ("diamond",     2),
    SAPPHIRE    ("sapphire",    3),
    PEARL       ("pearl",       4),
    AMETHYST    ("amethyst",    5),
    FIANIT      ("fianit",      6),
    EMERALD     ("emerald",     7),
    RUBY        ("ruby",        8),
    ;

    @JsonValue
    private final String key;
    private final int moonlightId;

    public static Optional<Treasure> findByKey(String key) {
        return KeyedEnumConstantFinder.findByKey(key, values());
    }

}
