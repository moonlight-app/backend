package ru.moonlightapp.backend.storage.converter;

import jakarta.persistence.Converter;
import ru.moonlightapp.backend.model.attribute.OrderStatus;

@Converter(autoApply = true)
public class OrderStatusConverter extends KeyedEnumConverterBase<OrderStatus> {

    @Override
    protected OrderStatus[] enumConstants() {
        return OrderStatus.values();
    }

}
