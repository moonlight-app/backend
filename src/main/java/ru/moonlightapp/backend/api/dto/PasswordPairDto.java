package ru.moonlightapp.backend.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springdoc.core.annotations.ParameterObject;

@ParameterObject
public record PasswordPairDto(
        @NotBlank @Size(min = 8, max = 128)
        String currentPassword,
        @NotBlank @Size(min = 8, max = 128)
        String newPassword
) {

}