package ru.moonlightapp.backend.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.moonlightapp.backend.validation.validator.EmailPatternValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmailPatternValidator.class)
public @interface EmailPattern {

    String message() default "Invalid email";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}