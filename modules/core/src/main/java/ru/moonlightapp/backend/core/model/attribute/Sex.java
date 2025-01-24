package ru.moonlightapp.backend.core.model.attribute;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.moonlightapp.backend.core.protobuf.model.attribute.ProtoSex;
import ru.moonlightapp.backend.core.util.KeyedEnumConstantFinder;

import java.util.Optional;

import static ru.moonlightapp.backend.core.protobuf.model.attribute.ProtoSex.SEX_FEMALE;
import static ru.moonlightapp.backend.core.protobuf.model.attribute.ProtoSex.SEX_MALE;

@Getter
@AllArgsConstructor
public enum Sex implements KeyedEnum {

    FEMALE  ("female"),
    MALE    ("male"),
    ;

    @JsonValue
    private final String key;

    public static Optional<Sex> findByKey(String key) {
        return KeyedEnumConstantFinder.findByKey(key, values());
    }

    public static Optional<Sex> fromProto(ProtoSex sex) {
        if (sex == null)
            return Optional.empty();

        return Optional.ofNullable(switch (sex) {
            case SEX_FEMALE -> FEMALE;
            case SEX_MALE -> MALE;
            default -> null;
        });
    }

    public static ProtoSex toProto(Sex sex) {
        if (sex == null)
            return ProtoSex.SEX_UNKNOWN;

        return sex.toProto();
    }

    public ProtoSex toProto() {
        return switch (this) {
            case FEMALE -> SEX_FEMALE;
            case MALE -> SEX_MALE;
        };
    }

}
