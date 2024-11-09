package ru.moonlightapp.backend.web.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.format.annotation.DateTimeFormat;
import ru.moonlightapp.backend.model.attribute.Sex;
import ru.moonlightapp.backend.validation.annotation.EmailPattern;

import java.time.LocalDate;

@ParameterObject
public record SignUpCompleteDto(
        @NotBlank @Size(min = 6, max = 64)
        @EmailPattern
        String email,
        @NotBlank @Size(min = 8, max = 128)
        String password,
        @NotBlank @Size(min = 1, max = 64)
        String name,
        @Past
        @DateTimeFormat(pattern = "dd.MM.yyyy")
        LocalDate birthDate,
        Sex sex
) {

}