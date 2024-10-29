package ru.moonlightapp.backend.model.attribute;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Treasure implements KeyedEnum {

    NOTHING     ("nothing"),
    DIAMOND     ("diamond"),
    SAPPHIRE    ("sapphire"),
    PEARL       ("pearl"),
    AMETHYST    ("amethyst"),
    FIANIT      ("fianit"),
    EMERALD     ("emerald"),
    RUBY        ("ruby"),
    ;

    @JsonValue
    private final String key;

    public static final class TreasureArrayConverter extends KeyedEnumArrayConverterBase<Treasure> {

        @Override
        protected Treasure parse(String string) {
            

            return null;
        }

        @Override
        protected Treasure[] createArray(int size) {
            return new Treasure[0];
        }

    }

}
