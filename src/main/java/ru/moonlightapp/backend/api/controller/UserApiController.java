package ru.moonlightapp.backend.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.moonlightapp.backend.api.dto.PasswordPairDto;
import ru.moonlightapp.backend.api.dto.UserDataDto;
import ru.moonlightapp.backend.api.model.UserModel;
import ru.moonlightapp.backend.api.service.UserService;
import ru.moonlightapp.backend.docs.annotation.BadRequestResponse;
import ru.moonlightapp.backend.docs.annotation.DescribeError;
import ru.moonlightapp.backend.docs.annotation.SuccessResponse;
import ru.moonlightapp.backend.exception.ApiException;
import ru.moonlightapp.backend.exception.GenericErrorException;
import ru.moonlightapp.backend.storage.model.User;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserApiController extends ApiControllerBase {

    private final UserService userService;

    @Operation(summary = "Получение профиля", tags = "user-api")
    @GetMapping
    public UserModel getProfile() throws ApiException {
        return UserModel.fromUser(getCurrentUser(userService));
    }

    @Operation(summary = "Изменение профиля", tags = "user-api")
    @SuccessResponse("Профиль пользователя изменен")
    @PatchMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void changeProfile(@Valid UserDataDto dto) throws GenericErrorException {
        User user = getCurrentUser(userService);
        userService.updateProfile(user, dto.name(), dto.birthDate(), dto.sex(), false);
    }

    @Operation(summary = "Изменение профиля", tags = "user-api")
    @SuccessResponse("Профиль пользователя изменен")
    @PutMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void setProfile(@Valid UserDataDto dto) throws GenericErrorException {
        User user = getCurrentUser(userService);
        userService.updateProfile(user, dto.name(), dto.birthDate(), dto.sex(), true);
    }

    @Operation(summary = "Изменение пароля пользователя", tags = "user-api")
    @SuccessResponse("Пароль пользователя изменен")
    @BadRequestResponse({"wrong_password"})
    @DescribeError(code = "wrong_password", message = "Неверный пароль")
    @PutMapping(value = "/password", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public void changePassword(@Valid PasswordPairDto dto) throws ApiException {
        User user = getCurrentUser(userService);
        userService.changePassword(user, dto.currentPassword(), dto.newPassword());
    }

}