package ru.moonlightapp.backend.api.dto;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.format.annotation.DateTimeFormat;
import ru.moonlightapp.backend.model.attribute.Sex;

import java.time.LocalDate;

@ParameterObject
public record UserDataDto(
        @Size(min = 1, max = 64)
        String name,
        @Past
        @DateTimeFormat(pattern = "dd.MM.yyyy")
        LocalDate birthDate,
        Sex sex
) {

}