package ru.moonlightapp.backend.core.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.moonlightapp.backend.core.validation.annotation.EmailPattern;

import java.util.Arrays;
import java.util.regex.Pattern;

public final class EmailPatternValidator implements ConstraintValidator<EmailPattern, Object> {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[_A-Za-z0-9-+]+(?>\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(?>\\.[A-Za-z0-9]+)*(?>\\.[A-Za-z]{2,})$");

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        return isValidEmail(object);
    }

    public static boolean isValidEmail(Object object) {
        if (object instanceof String email) {
            return hasValidEmailPattern(email);
        } else if (object instanceof String[] emails) {
            return Arrays.stream(emails).allMatch(EmailPatternValidator::hasValidEmailPattern);
        } else {
            return true;
        }
    }

    public static boolean hasValidEmailPattern(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

}