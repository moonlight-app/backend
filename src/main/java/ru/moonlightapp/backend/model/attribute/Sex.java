package ru.moonlightapp.backend.model.attribute;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.moonlightapp.backend.util.KeyedEnumConstantFinder;

import java.util.Optional;

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

}
