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
    SAPPHIRE    ("sapphire",    4),
    PEARL       ("pearl",       8),
    AMETHYST    ("amethyst",    16),
    FIANIT      ("fianit",      32),
    EMERALD     ("emerald",     64),
    RUBY        ("ruby",        128),
    ;

    @JsonValue
    private final String key;
    private final int moonlightBit;

    public static Optional<Treasure> findByKey(String key) {
        return KeyedEnumConstantFinder.findByKey(key, values());
    }

}
