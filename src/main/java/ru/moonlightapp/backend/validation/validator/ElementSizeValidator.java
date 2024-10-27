package ru.moonlightapp.backend.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.moonlightapp.backend.validation.annotation.ElementSize;

public final class ElementSizeValidator implements ConstraintValidator<ElementSize, Object> {

    private int min;
    private int max;
    private boolean skipEmpty;

    @Override
    public void initialize(ElementSize parameters) {
        this.min = parameters.min();
        this.max = parameters.max();
        this.skipEmpty = parameters.skipEmpty();
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        if (object instanceof Object[] array) {
            for (Object element : array)
                if (!isValidElement(element, context))
                    return false;

            return true;
        }

        if (object instanceof Iterable<?> iterable) {
            for (Object element : iterable)
                if (!isValidElement(element, context))
                    return false;

            return true;
        }

        return true;
    }

    private boolean isValidElement(Object element, ConstraintValidatorContext context) {
        if (element instanceof CharSequence charSequence) {
            int length = charSequence.length();
            return (length == 0 && skipEmpty) || length >= min && length <= max;
        }

        return isValid(element, context);
    }

}