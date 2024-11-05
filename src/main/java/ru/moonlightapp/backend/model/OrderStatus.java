package ru.moonlightapp.backend.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatus {

    CLOSED("closed"),
    IN_DELIVERY("in_delivery"),
    ;

    @JsonValue
    private final String key;

}
