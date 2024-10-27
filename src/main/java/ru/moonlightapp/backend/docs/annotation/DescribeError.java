package ru.moonlightapp.backend.docs.annotation;

import java.lang.annotation.*;

@Inherited
@Repeatable(DescribeErrors.class)
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DescribeError {

    String code();

    String message() default "—";

    boolean system() default false;

    String payload() default "—";

}