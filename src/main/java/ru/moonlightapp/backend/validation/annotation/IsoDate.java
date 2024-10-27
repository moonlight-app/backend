package ru.moonlightapp.backend.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.moonlightapp.backend.validation.validator.IsoDateValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IsoDateValidator.class)
public @interface IsoDate {

    String message() default "Invalid ISO date";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}