package ru.moonlightapp.backend.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;

@Getter
@AllArgsConstructor
public enum Sex {

    FEMALE  ("female"),
    MALE    ("male"),
    ;

    @JsonValue
    private final String key;

    public static Optional<Sex> findByKey(String key) {
        if (key != null && !key.isEmpty())
            for (Sex value : values())
                if (key.equalsIgnoreCase(value.getKey()))
                    return Optional.of(value);

        return Optional.empty();
    }

}
