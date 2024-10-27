package ru.moonlightapp.backend.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.validation.FieldError;
import ru.moonlightapp.backend.model.ErrorModel;

import java.util.Set;

@Getter
public class GenericErrorException extends Exception {

    protected final HttpStatusCode statusCode;
    protected final String errorCode;
    protected final String errorMessage;

    public GenericErrorException(Throwable cause) {
        this(HttpStatus.INTERNAL_SERVER_ERROR, "unexpected_error", "An unexpected error was occured! Try again later.", cause);
    }

    public GenericErrorException(String errorCode, String errorMessage) {
        this(HttpStatus.BAD_REQUEST, errorCode, errorMessage, null);
    }

    public GenericErrorException(HttpStatusCode statusCode, String errorCode, String errorMessage) {
        this(statusCode, errorCode, errorMessage, null);
    }

    public GenericErrorException(HttpStatusCode statusCode, String errorCode, String errorMessage, Throwable cause) {
        super(errorMessage, cause);
        this.statusCode = statusCode;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public static GenericErrorException fromFieldError(FieldError fieldError) {
        String fieldName = fieldError.getField();
        String message = fieldError.getDefaultMessage();
        return new GenericErrorException(HttpStatus.BAD_REQUEST, "incorrect_field_value", "%s: %s".formatted(fieldName, message));
    }

    public static GenericErrorException fromConstraintViolation(String propertyName, String message) {
        return new GenericErrorException(
                HttpStatus.BAD_REQUEST,
                "incorrect_field_value",
                "%s: %s".formatted(propertyName, message)
        );
    }

    public static GenericErrorException fromConstraintViolation(ConstraintViolation<?> violation) {
        String propertyName = violation.getPropertyPath().toString();
        String message = violation.getMessage();
        return fromConstraintViolation(propertyName, message);
    }

    public static <T> void throwIfViolationsFound(Validator validator, T validationTarget) throws GenericErrorException {
        Set<ConstraintViolation<T>> violations = validator.validate(validationTarget);
        if (!violations.isEmpty()) {
            ConstraintViolation<T> first = violations.stream().limit(1).findFirst().orElse(null);
            throw GenericErrorException.fromConstraintViolation(first);
        }
    }

    public ErrorModel constructModel() {
        return new ErrorModel(errorCode, errorMessage);
    }

}