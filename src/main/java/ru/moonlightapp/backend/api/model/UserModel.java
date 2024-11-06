package ru.moonlightapp.backend.api.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.moonlightapp.backend.model.attribute.Sex;
import ru.moonlightapp.backend.storage.model.User;

import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserModel(
        @JsonProperty("email") String email,
        @JsonProperty("name") String name,
        @JsonProperty("birth_date") @JsonFormat(pattern = "dd.MM.yyyy") LocalDate birthDate,
        @JsonProperty("sex") Sex sex
) {

    public static UserModel from(User user) {
        return new UserModel(user.getEmail(), user.getName(), user.getBirthDate(), user.getSex());
    }

}