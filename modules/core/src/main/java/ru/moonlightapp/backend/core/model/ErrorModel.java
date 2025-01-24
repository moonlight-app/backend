package ru.moonlightapp.backend.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import ru.moonlightapp.backend.core.protobuf.model.ErrorModel.ProtoErrorModel;

import java.util.Optional;

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

    public static Optional<ErrorModel> fromProto(ProtoErrorModel errorModel) {
        if (errorModel == null)
            return Optional.empty();

        return Optional.of(new ErrorModel(
                errorModel.getCode(),
                errorModel.getMessage(),
                null
        ));
    }

    public static ProtoErrorModel toProto(ErrorModel errorModel) {
        if (errorModel == null)
            return null;

        return errorModel.toProto();
    }

    public ProtoErrorModel toProto() {
        return ProtoErrorModel.newBuilder()
                .setCode(errorCode)
                .setMessage(errorMessage)
                .build();
    }

}
