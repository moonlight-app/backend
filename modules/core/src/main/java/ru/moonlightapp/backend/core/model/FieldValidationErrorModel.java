package ru.moonlightapp.backend.core.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.moonlightapp.backend.core.protobuf.model.ErrorModel.ProtoErrorModel;

public class FieldValidationErrorModel extends ErrorModel {

    @JsonProperty("field_name")
    private final String fieldName;

    public FieldValidationErrorModel(String fieldName, String message) {
        super("incorrect_field_value", message);
        this.fieldName = fieldName;
    }

    @Override
    public String toString() {
        return "FieldValidationErrorModel{" +
                "fieldName='" + fieldName + '\'' +
                ", errorCode='" + errorCode + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", payload=" + payload +
                '}';
    }

    @Override
    public ProtoErrorModel toProto() {
        return ProtoErrorModel.newBuilder()
                .setCode(errorCode)
                .setMessage(errorMessage)
                .setFieldName(fieldName)
                .build();
    }

}