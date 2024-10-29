package ru.moonlightapp.backend.model.attribute;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.moonlightapp.backend.util.KeyedEnumConstantFinder;

import java.util.Optional;

@Getter
@AllArgsConstructor
public enum Audience implements KeyedEnum {

    MEN         ("men"),
    WOMEN       ("women"),
    CHILDREN    ("children"),
    UNISEX      ("unisex"),
    ;

    @JsonValue
    private final String key;

    public static Optional<Audience> findByKey(String key) {
        return KeyedEnumConstantFinder.findByKey(key, values());
    }

}
