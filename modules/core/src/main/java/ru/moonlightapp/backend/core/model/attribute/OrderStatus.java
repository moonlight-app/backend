package ru.moonlightapp.backend.core.model.attribute;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.moonlightapp.backend.core.protobuf.model.attribute.ProtoOrderStatus;
import ru.moonlightapp.backend.core.util.KeyedEnumConstantFinder;

import java.util.Optional;

import static ru.moonlightapp.backend.core.protobuf.model.attribute.ProtoOrderStatus.ORDER_STATUS_CLOSED;
import static ru.moonlightapp.backend.core.protobuf.model.attribute.ProtoOrderStatus.ORDER_STATUS_IN_DELIVERY;

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

    public static Optional<OrderStatus> fromProto(ProtoOrderStatus orderStatus) {
        if (orderStatus == null)
            return Optional.empty();

        return Optional.ofNullable(switch (orderStatus) {
            case ORDER_STATUS_IN_DELIVERY -> IN_DELIVERY;
            case ORDER_STATUS_CLOSED -> CLOSED;
            default -> null;
        });
    }

    public static ProtoOrderStatus toProto(OrderStatus material) {
        if (material == null)
            return ProtoOrderStatus.ORDER_STATUS_UNKNOWN;

        return material.toProto();
    }

    public ProtoOrderStatus toProto() {
        return switch (this) {
            case IN_DELIVERY -> ORDER_STATUS_IN_DELIVERY;
            case CLOSED -> ORDER_STATUS_CLOSED;
        };
    }

}
