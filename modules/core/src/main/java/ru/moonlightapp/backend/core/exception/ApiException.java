package ru.moonlightapp.backend.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import ru.moonlightapp.backend.core.model.ErrorModel;

import java.text.MessageFormat;

public class ApiException extends GenericErrorException {

    private Object payload;

    public ApiException(String errorCode, String errorMessage, Object... args) {
        this(HttpStatus.BAD_REQUEST, errorCode, errorMessage, args);
    }

    public ApiException(HttpStatusCode statusCode, String errorCode, String errorMessage, Object... args) {
        super(statusCode, errorCode, formatMessage(errorMessage, args));
    }

    public ApiException withPayload(Object payload) {
        this.payload = payload;
        return this;
    }

    private static String formatMessage(String errorMessage, Object... args) {
        if (errorMessage == null || errorMessage.isEmpty() || args == null || args.length == 0)
            return errorMessage;

        return MessageFormat.format(errorMessage, args);
    }

    @Override
    public ErrorModel constructModel() {
        return new ErrorModel(errorCode, errorMessage, payload);
    }

}