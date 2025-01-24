package ru.moonlightapp.backend.core.model.attribute;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.moonlightapp.backend.core.protobuf.model.attribute.ProtoProductType;
import ru.moonlightapp.backend.core.util.KeyedEnumConstantFinder;

import java.util.Optional;

import static ru.moonlightapp.backend.core.protobuf.model.attribute.ProtoProductType.*;

@Getter
@AllArgsConstructor
public enum ProductType implements KeyedEnum {

    RING        ("ring",        1),
    BRACELET    ("bracelet",    2),
    CHAIN       ("chain",       4),
    WATCH       ("watch",       8),
    EARRINGS    ("earrings",    16),
    NECKLACE    ("necklace",    32),
    ;

    @JsonValue
    private final String key;
    private final int moonlightBit;

    public static Optional<ProductType> findByKey(String key) {
        return KeyedEnumConstantFinder.findByKey(key, values());
    }

    public static Optional<ProductType> fromProto(ProtoProductType productType) {
        if (productType == null)
            return Optional.empty();

        return Optional.ofNullable(switch (productType) {
            case PRODUCT_TYPE_RING -> RING;
            case PRODUCT_TYPE_BRACELET -> BRACELET;
            case PRODUCT_TYPE_CHAIN -> CHAIN;
            case PRODUCT_TYPE_WATCH -> WATCH;
            case PRODUCT_TYPE_EARRINGS -> EARRINGS;
            case PRODUCT_TYPE_NECKLACE -> NECKLACE;
            default -> null;
        });
    }

    public static ProtoProductType toProto(ProductType productType) {
        if (productType == null)
            return ProtoProductType.PRODUCT_TYPE_UNKNOWN;

        return productType.toProto();
    }

    public ProtoProductType toProto() {
        return switch (this) {
            case RING -> PRODUCT_TYPE_RING;
            case BRACELET -> PRODUCT_TYPE_BRACELET;
            case CHAIN -> PRODUCT_TYPE_CHAIN;
            case WATCH -> PRODUCT_TYPE_WATCH;
            case EARRINGS -> PRODUCT_TYPE_EARRINGS;
            case NECKLACE -> PRODUCT_TYPE_NECKLACE;
        };
    }

}
