package ru.moonlightapp.backend.web.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.moonlightapp.backend.docs.annotation.*;
import ru.moonlightapp.backend.exception.ApiException;
import ru.moonlightapp.backend.model.TokenPairModel;
import ru.moonlightapp.backend.service.JwtTokenService;
import ru.moonlightapp.backend.storage.model.TokenPair;
import ru.moonlightapp.backend.web.auth.dto.*;
import ru.moonlightapp.backend.web.auth.service.RecoveryService;
import ru.moonlightapp.backend.web.auth.service.RegistrationService;

import static ru.moonlightapp.backend.web.auth.service.EmailConfirmationService.ProofKeyConsumer.httpHeaderBased;
import static ru.moonlightapp.backend.web.auth.service.EmailConfirmationService.extractProofKey;

@DescribeError(code = "email_already_used", message = "Адрес эл. почты уже используется")
@DescribeError(code = "email_already_confirmed", system = true, message = "Адрес эл. почты уже подтвержден")
@DescribeError(code = "email_confirmation_unrenewable", system = true, message = "Подтверждение не может быть возобновлено")
@DescribeError(code = "email_confirmation_pending", system = true, message = "Адрес эл. почты уже в процессе подтверждения")
@DescribeError(code = "email_not_confirmed", system = true, message = "Операция требует подтверждения по эл. почте")
@DescribeError(code = "no_more_attempts", message = "Попытки ввода кода подтверждения закончились")
@DescribeError(code = "request_expired", system = true, message = "Запрос подтверждения истек")
@DescribeError(code = "request_not_found", system = true, message = "Запрос подтверждения не найден")
@DescribeError(code = "token_pair_not_found", system = true, message = "Неизвестная пара токенов")
@DescribeError(code = "wrong_code", message = "Неверный код подтверждения", payload = "`attemptsLeft`")
@DescribeError(code = "wrong_proof_key", system = true, message = "Неверный ключ подтверждения")
@DescribeError(code = "user_not_found", system = true, message = "Пользователь не найден")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public final class AuthProcessingController {

    private final RegistrationService registrationService;
    private final RecoveryService recoveryService;
    private final JwtTokenService jwtTokenService;

    @Operation(summary = "Вход в аккаунт", tags = "auth-api")
    @ApiResponse(responseCode = "302", description = "Вход выполнен, переадресация на `/workspace`")
    @BadRequestResponse({"bad_credentials", "provider_not_found", "unexpected_error"})
    @DescribeError(code = "bad_credentials", message = "Неверные имя пользователя или пароль")
    @DescribeError(code = "provider_not_found", system = true, message = "Провайдер аутентификации не найден")
    @DescribeError(code = "unexpected_error", system = true, message = "Произошла неожиданная ошибка")
    @PostMapping(value = "/sign-in", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseStatus(HttpStatus.I_AM_A_TEAPOT)
    public void processSignIn(@Valid SignInDto dto) {}

    @Operation(summary = "Запрос кода подтверждения регистрации", tags = "auth-api")
    @SuccessResponse("Код подтверждения регистрации отправлен")
    @Note("Возвращает ключ подтверждения в заголовке `X-Proof-Key`")
    @BadRequestResponse({"email_already_used", "email_already_confirmed", "email_confirmation_unrenewable", "email_confirmation_pending"})
    @PostMapping(value = "/sign-up/email-code", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void processSignUpCodeRequest(@Valid SignUpCodeRequestDto dto, HttpServletResponse response) throws ApiException {
        registrationService.processConfirmationRequest(dto.email(), dto.name(), dto.isRenew(), httpHeaderBased(response));
    }

    @Operation(summary = "Подтверждение регистрации с помощью кода", tags = "auth-api")
    @SuccessResponse("Выполнено подтверждение почты для регистрации аккаунта")
    @Note("Требует ключ подтверждения в заголовке `X-Proof-Key`")
    @BadRequestResponse({"request_not_found", "request_expired", "wrong_proof_key", "no_more_attempts", "wrong_code"})
    @PostMapping(value = "/sign-up/confirm-email", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void processSignUpConfirmation(@Valid SignUpConfirmationDto dto, HttpServletRequest request) throws ApiException {
        registrationService.processEmailConfirmation(dto.email(), dto.code(), extractProofKey(request));
    }

    @Operation(summary = "Регистрация пользователя", tags = "auth-api")
    @SuccessResponse("Новый пользователь зарегистрирован")
    @Note("Требует ключ подтверждения в заголовке `X-Proof-Key`")
    @BadRequestResponse({"user_already_exists", "email_not_confirmed", "wrong_proof_key"})
    @DescribeError(code = "user_already_exists", system = true, message = "Пользователь с таким адресом эл. почты уже зарегистрирован")
    @PostMapping(value = "/sign-up/complete", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseStatus(HttpStatus.TEMPORARY_REDIRECT)
    public void processSignUpComplete(@Valid SignUpCompleteDto dto, HttpServletRequest request, HttpServletResponse response) throws ApiException {
        registrationService.registerNewUser(
                dto.email(), dto.password(), extractProofKey(request),
                dto.name(), dto.birthDate(), dto.sex()
        );

        response.setHeader("Location", "/auth/sign-in");
    }

    @Operation(summary = "Запрос кода восстановления доступа", tags = "auth-api")
    @SuccessResponse("Код восстановления доступа отправлен")
    @Note("Возвращает ключ подтверждения в заголовке `X-Proof-Key`")
    @BadRequestResponse({"user_not_found", "email_already_confirmed", "email_confirmation_unrenewable", "email_confirmation_pending"})
    @PostMapping(value = "/recovery/email-code", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void processRecoveryCodeRequest(@Valid RecoveryCodeRequestDto dto, HttpServletResponse response) throws ApiException {
        recoveryService.processConfirmationRequest(dto.email(), dto.isRenew(), httpHeaderBased(response));
    }

    @Operation(summary = "Подтверждение восстановления с помощью кода", tags = "auth-api")
    @SuccessResponse("Выполнено подтверждение почты для восстановления доступа")
    @Note("Требует ключ подтверждения в заголовке `X-Proof-Key`")
    @BadRequestResponse({"user_not_found", "request_not_found", "request_expired", "wrong_proof_key", "no_more_attempts", "wrong_code"})
    @PostMapping(value = "/recovery/confirm-email", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void processRecoveryConfirmation(@Valid RecoveryConfirmationDto dto, HttpServletRequest request) throws ApiException {
        recoveryService.processEmailConfirmation(dto.email(), dto.code(), extractProofKey(request));
    }

    @Operation(summary = "Восстановление доступа", tags = "auth-api")
    @SuccessResponse("Пароль успешно изменен")
    @Note("Требует ключ подтверждения в заголовке `X-Proof-Key`")
    @BadRequestResponse({"user_not_found", "email_not_confirmed", "wrong_proof_key"})
    @PostMapping(value = "/recovery/change-password", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void processRecoveryChangePassword(@Valid RecoveryChangePasswordDto dto, HttpServletRequest request) throws ApiException {
        recoveryService.performRecovery(dto.email(), dto.password(), extractProofKey(request));
    }

    @Operation(summary = "Обновление токена доступа", tags = "auth-api")
    @SuccessResponse("Получена новая пара токенов")
    @BadRequestResponse({"token_pair_not_found", "token_is_expired", "user_not_found"})
    @DescribeError(code = "token_is_expired", system = true, message = "Токен обновления истек")
    @PostMapping(value = "/token/refresh")
    public TokenPairModel refreshToken(@RequestParam("refresh_token") String refreshToken, HttpServletRequest request) throws ApiException {
        String accessToken = JwtTokenService.tryFetchBearerToken(request).orElseThrow();
        TokenPair tokenPair = jwtTokenService.refreshTokenPair(accessToken, refreshToken);
        return new TokenPairModel(
                tokenPair.getAccessToken(), jwtTokenService.getJwtAccessTokenLifetime(),
                tokenPair.getRefreshToken(), jwtTokenService.getJwtRefreshTokenLifetime()
        );
    }

    @Operation(summary = "Проверка токена доступа", tags = "auth-api")
    @SuccessResponse("Токен доступа действителен")
    @ForbiddenResponse("token_is_expired")
    @DescribeError(code = "token_is_expired", system = true, message = "Токен доступа истек")
    @PostMapping(value = "/token/validate")
    @ResponseStatus(HttpStatus.OK)
    public void validateToken(HttpServletRequest request) throws ApiException {
        String accessToken = JwtTokenService.tryFetchBearerToken(request).orElseThrow();
        jwtTokenService.validateAccessToken(accessToken);
    }

}