package ru.moonlightapp.backend.api.dto;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import org.springdoc.core.annotations.ParameterObject;
import ru.moonlightapp.backend.model.Sex;

import java.time.LocalDate;

@ParameterObject
public record UserDataDto(
        @Size(min = 1, max = 64)
        String name,
        @Past
        @Temporal(TemporalType.DATE)
        LocalDate birthDate,
        Sex sex
) {

}