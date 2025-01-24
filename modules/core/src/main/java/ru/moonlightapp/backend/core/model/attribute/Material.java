package ru.moonlightapp.backend.core.model.attribute;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.moonlightapp.backend.core.protobuf.model.attribute.ProtoMaterial;
import ru.moonlightapp.backend.core.util.KeyedEnumConstantFinder;

import java.util.Optional;

import static ru.moonlightapp.backend.core.protobuf.model.attribute.ProtoMaterial.*;

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

    public static Optional<Material> fromProto(ProtoMaterial material) {
        if (material == null)
            return Optional.empty();

        return Optional.ofNullable(switch (material) {
            case MATERIAL_GOLD -> GOLD;
            case MATERIAL_SILVER -> SILVER;
            case MATERIAL_PLATINUM -> PLATINUM;
            case MATERIAL_WHITE_GOLD -> WHITE_GOLD;
            case MATERIAL_PINK_GOLD -> PINK_GOLD;
            case MATERIAL_CERAMICS -> CERAMICS;
            default -> null;
        });
    }

    public static ProtoMaterial toProto(Material material) {
        if (material == null)
            return ProtoMaterial.MATERIAL_UNKNOWN;

        return material.toProto();
    }

    public ProtoMaterial toProto() {
        return switch (this) {
            case GOLD -> MATERIAL_GOLD;
            case SILVER -> MATERIAL_SILVER;
            case PLATINUM -> MATERIAL_PLATINUM;
            case WHITE_GOLD -> MATERIAL_WHITE_GOLD;
            case PINK_GOLD -> MATERIAL_PINK_GOLD;
            case CERAMICS -> MATERIAL_CERAMICS;
        };
    }

}
