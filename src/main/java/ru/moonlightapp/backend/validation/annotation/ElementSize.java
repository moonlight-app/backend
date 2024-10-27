package ru.moonlightapp.backend.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.moonlightapp.backend.validation.validator.ElementSizeValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ElementSizeValidator.class)
public @interface ElementSize {

    String message() default "{jakarta.validation.constraints.Size.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int min() default 0;

    int max() default Integer.MAX_VALUE;

    boolean skipEmpty() default false;

}
