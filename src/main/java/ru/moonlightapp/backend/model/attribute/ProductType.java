package ru.moonlightapp.backend.model.attribute;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductType {

    RING        ("ring"),
    BRACELET    ("bracelet"),
    CHAIN       ("chain"),
    WATCH       ("watch"),
    EARRINGS    ("earrings"),
    NECKLACE    ("necklace"),
    ;

    @JsonValue
    private final String key;

}
