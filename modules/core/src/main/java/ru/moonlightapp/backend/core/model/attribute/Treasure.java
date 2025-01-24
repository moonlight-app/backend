package ru.moonlightapp.backend.core.model.attribute;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.moonlightapp.backend.core.protobuf.model.attribute.ProtoTreasure;
import ru.moonlightapp.backend.core.util.KeyedEnumConstantFinder;

import java.util.Optional;

import static ru.moonlightapp.backend.core.protobuf.model.attribute.ProtoTreasure.*;

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

    public static Optional<Treasure> fromProto(ProtoTreasure treasure) {
        if (treasure == null)
            return Optional.empty();

        return Optional.ofNullable(switch (treasure) {
            case TREASURE_NOTHING -> NOTHING;
            case TREASURE_DIAMOND -> DIAMOND;
            case TREASURE_SAPPHIRE -> SAPPHIRE;
            case TREASURE_PEARL -> PEARL;
            case TREASURE_AMETHYST -> AMETHYST;
            case TREASURE_FIANIT -> FIANIT;
            case TREASURE_EMERALD -> EMERALD;
            case TREASURE_RUBY -> RUBY;
            default -> null;
        });
    }

    public static ProtoTreasure toProto(Treasure treasure) {
        if (treasure == null)
            return ProtoTreasure.TREASURE_UNKNOWN;

        return treasure.toProto();
    }

    public ProtoTreasure toProto() {
        return switch (this) {
            case NOTHING -> TREASURE_NOTHING;
            case DIAMOND -> TREASURE_DIAMOND;
            case SAPPHIRE -> TREASURE_SAPPHIRE;
            case PEARL -> TREASURE_PEARL;
            case AMETHYST -> TREASURE_AMETHYST;
            case FIANIT -> TREASURE_FIANIT;
            case EMERALD -> TREASURE_EMERALD;
            case RUBY -> TREASURE_RUBY;
        };
    }

}
