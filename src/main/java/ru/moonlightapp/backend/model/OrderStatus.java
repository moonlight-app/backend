package ru.moonlightapp.backend.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatus {

    IN_DELIVERY("in_delivery"),
    CLOSED("closed"),
    ;

    @JsonValue
    private final String key;

}
