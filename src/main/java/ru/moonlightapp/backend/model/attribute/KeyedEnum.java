package ru.moonlightapp.backend.model.attribute;

import jakarta.persistence.AttributeConverter;
import org.apache.logging.log4j.util.Strings;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public interface KeyedEnum {

    String getKey();

    abstract class KeyedEnumArrayConverterBase<E extends KeyedEnum> implements AttributeConverter<String, E[]> {

        protected abstract E parse(String string);

        protected abstract E[] createArray(int size);

        @Override
        public E[] convertToDatabaseColumn(String string) {
            if (string == null || string.isEmpty())
                return null;

            return Arrays.stream(string.split(","))
                    .filter(Strings::isNotEmpty)
                    .map(this::parse)
                    .filter(Objects::nonNull)
                    .toArray(this::createArray);
        }

        @Override
        public String convertToEntityAttribute(E[] array) {
            if (array == null || array.length == 0)
                return null;

            return Arrays.stream(array)
                    .filter(Objects::nonNull)
                    .map(KeyedEnum::getKey)
                    .collect(Collectors.joining(","));
        }

    }

}
