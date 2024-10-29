package ru.moonlightapp.backend.model.attribute;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Audience {

    MEN         ("men"),
    WOMEN       ("women"),
    CHILDREN    ("children"),
    UNISEX      ("unisex"),
    ;

    @JsonValue
    private final String key;

}
