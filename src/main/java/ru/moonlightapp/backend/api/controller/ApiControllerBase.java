package ru.moonlightapp.backend.api.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import ru.moonlightapp.backend.api.service.UserService;
import ru.moonlightapp.backend.exception.ApiException;
import ru.moonlightapp.backend.storage.model.User;

public abstract class ApiControllerBase {

    protected final String getCurrentUsername() throws ApiException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails)
            return userDetails.getUsername();

        throw new ApiException("no_current_username", "User not found for the current authentication");
    }

    protected final User getCurrentUser(UserService userService) throws ApiException {
        String email = getCurrentUsername();
        return userService.findByEmail(email).orElseThrow(() -> new ApiException(
                "no_current_user",
                "The user was not found for the current authentication"
        ));
    }

    protected final String requireUserExistance(UserService userService) throws ApiException {
        String email = getCurrentUsername();
        if (!userService.isUserExist(email))
            throw new ApiException("no_current_user", "User not found for the current authentication");

        return email;
    }

}
