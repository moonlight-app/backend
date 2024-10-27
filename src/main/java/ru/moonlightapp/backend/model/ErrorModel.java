package ru.moonlightapp.backend.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class ErrorModel {

    @JsonProperty("error_code")
    protected final String errorCode;
    @JsonProperty("error_message")
    protected final String errorMessage;
    @JsonProperty("payload")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected final Object payload;

    public ErrorModel(String errorCode, String errorMessage) {
        this(errorCode, errorMessage, null);
    }

    public ErrorModel(String errorCode, String errorMessage, Object payload) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "ErrorModel{" +
                "errorCode='" + errorCode + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", payload=" + payload +
                '}';
    }

}
