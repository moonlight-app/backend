package ru.moonlightapp.backend.core.model.attribute;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.moonlightapp.backend.core.protobuf.model.attribute.ProtoAudience;
import ru.moonlightapp.backend.core.util.KeyedEnumConstantFinder;

import java.util.Optional;

import static ru.moonlightapp.backend.core.protobuf.model.attribute.ProtoAudience.*;

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

    public static Optional<Audience> fromProto(ProtoAudience audience) {
        if (audience == null)
            return Optional.empty();

        return Optional.ofNullable(switch (audience) {
            case AUDIENCE_MEN -> MEN;
            case AUDIENCE_WOMEN -> WOMEN;
            case AUDIENCE_CHILDREN -> CHILDREN;
            case AUDIENCE_UNISEX -> UNISEX;
            default -> null;
        });
    }

    public static ProtoAudience toProto(Audience audience) {
        if (audience == null)
            return AUDIENCE_UNKNOWN;

        return audience.toProto();
    }

    public ProtoAudience toProto() {
        return switch (this) {
            case MEN -> AUDIENCE_MEN;
            case WOMEN -> AUDIENCE_WOMEN;
            case CHILDREN -> AUDIENCE_CHILDREN;
            case UNISEX -> AUDIENCE_UNISEX;
        };
    }

}
