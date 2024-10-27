package ru.moonlightapp.backend.docs.annotation;

import java.lang.annotation.*;

@Inherited
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DescribeErrors {

    DescribeError[] value() default {};

}