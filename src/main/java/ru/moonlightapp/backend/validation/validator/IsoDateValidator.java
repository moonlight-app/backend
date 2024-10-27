package ru.moonlightapp.backend.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.moonlightapp.backend.validation.annotation.IsoDate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class IsoDateValidator implements ConstraintValidator<IsoDate, String> {

    @Override
    public boolean isValid(String isoDate, ConstraintValidatorContext context) {
        return isIsoDate(isoDate);
    }

    public static boolean isIsoDate(String isoDate) {
        if (isoDate != null) {
            try {
                return LocalDate.parse(isoDate, DateTimeFormatter.ISO_DATE) != null;
            } catch (DateTimeParseException ignored) {
            }
        }

        return false;
    }

}