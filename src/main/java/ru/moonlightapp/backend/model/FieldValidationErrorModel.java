package ru.moonlightapp.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;

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

}