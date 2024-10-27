package ru.moonlightapp.backend.web;

import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.moonlightapp.backend.exception.ApiException;
import ru.moonlightapp.backend.exception.GenericErrorException;
import ru.moonlightapp.backend.model.ErrorModel;
import ru.moonlightapp.backend.model.FieldValidationErrorModel;

@ControllerAdvice
public final class MoonlightExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({ApiException.class, GenericErrorException.class})
    public ResponseEntity<Object> handleGenericError(GenericErrorException ex) {
        return ResponseEntity.status(ex.getStatusCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(ex.constructModel());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        FieldError fieldError = ex.getBindingResult().getFieldError();

        ErrorModel body = fieldError != null
                ? new FieldValidationErrorModel(fieldError.getField(), fieldError.getDefaultMessage())
                : new ErrorModel("incorrect_field_value", "FieldError instance isn't provided!");

        return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    @Override
    protected ResponseEntity<Object> createResponseEntity(Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        if (body instanceof ProblemDetail cast)
            return ResponseEntity.status(cast.getStatus()).build();

        return super.createResponseEntity(body, headers, statusCode, request);
    }

}