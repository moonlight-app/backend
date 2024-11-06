package ru.moonlightapp.backend.model.attribute;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.moonlightapp.backend.util.KeyedEnumConstantFinder;

import java.util.Optional;

@Getter
@AllArgsConstructor
public enum OrderStatus implements KeyedEnum {

    IN_DELIVERY ("in_delivery"),
    CLOSED      ("closed"),
    ;

    @JsonValue
    private final String key;

    public static Optional<OrderStatus> findByKey(String key) {
        return KeyedEnumConstantFinder.findByKey(key, values());
    }

}
