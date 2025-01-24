package ru.moonlightapp.backend.service.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springdoc.core.annotations.ParameterObject;
import ru.moonlightapp.backend.core.validation.annotation.EmailPattern;

@ParameterObject
public record SignInDto(
        @NotBlank @Size(min = 6, max = 64)
        @EmailPattern
        String email,
        @NotBlank @Size(min = 8, max = 128)
        String password
) {

}