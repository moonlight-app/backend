package ru.moonlightapp.backend.core.docs.annotation;

import java.lang.annotation.*;

@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SuccessResponse {

    String value() default "ОК";

    boolean canBeEmpty() default false;

}