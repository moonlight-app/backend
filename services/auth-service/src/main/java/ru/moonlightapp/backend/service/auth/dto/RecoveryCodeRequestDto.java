package ru.moonlightapp.backend.service.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springdoc.core.annotations.ParameterObject;
import ru.moonlightapp.backend.core.validation.annotation.EmailPattern;

@ParameterObject
public record RecoveryCodeRequestDto(
        @NotBlank @Size(min = 6, max = 64)
        @EmailPattern
        String email,
        @Schema(description = "Опциональный флаг 'renew' для обновления время действия подтверждения")
        String renew
) {

    public boolean isRenew() {
        return "true".equalsIgnoreCase(renew);
    }

}