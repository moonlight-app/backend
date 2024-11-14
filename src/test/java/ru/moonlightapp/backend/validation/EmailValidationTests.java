package ru.moonlightapp.backend.validation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static ru.moonlightapp.backend.validation.validator.EmailPatternValidator.isValidEmail;

public final class EmailValidationTests {

    @Test
    void whenRealEmailsPassed_thenConsideredValid() {
        assertTrue(isValidEmail("partners@easylauncher.org"));
        assertTrue(isValidEmail("sample-mail@yandex.ru"));
        assertTrue(isValidEmail("moonlight.mail.agent@gmail.com"));
    }

    @Test
    void whenMalformedEmailsPassed_thenConsideredInvalid() {
        assertFalse(isValidEmail("partners@.org"));
        assertFalse(isValidEmail("partners@easylauncher"));
        assertFalse(isValidEmail("i don't know"));
    }

    @Test
    void whenEmptyStringPassed_thenConsideredInvalid() {
        assertFalse(isValidEmail(""));
    }

    @Test
    void whenNullPassed_thenConsideredValid() {
        assertTrue(isValidEmail(null));
    }

}
