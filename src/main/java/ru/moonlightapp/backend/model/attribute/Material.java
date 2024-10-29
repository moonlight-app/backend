package ru.moonlightapp.backend.model.attribute;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Material {

    GOLD        ("gold"),
    SILVER      ("silver"),
    PLATINUM    ("platinum"),
    WHITE_GOLD  ("white_gold"),
    PINK_GOLD   ("pink_gold"),
    CERAMICS    ("ceramics"),
    ;

    @JsonValue
    private final String key;

}
