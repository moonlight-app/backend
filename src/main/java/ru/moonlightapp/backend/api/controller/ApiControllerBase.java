package ru.moonlightapp.backend.api.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import ru.moonlightapp.backend.api.service.UserService;
import ru.moonlightapp.backend.exception.ApiException;
import ru.moonlightapp.backend.storage.model.User;

import java.util.Optional;

public abstract class ApiControllerBase {

    protected final Optional<String> findCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails)
            return Optional.ofNullable(userDetails.getUsername());

        return Optional.empty();
    }

    protected final Optional<User> findCurrentUser(UserService userService) {
        return findCurrentUsername().flatMap(userService::findByEmail);
    }

    protected final String getCurrentUsername() throws ApiException {
        return findCurrentUsername().orElseThrow(() -> new ApiException(
                "no_current_username",
                "User not found for the current authentication!"
        ));
    }

    protected final User getCurrentUser(UserService userService) throws ApiException {
        String email = getCurrentUsername();
        return findCurrentUser(userService).orElseThrow(() -> new ApiException(
                "no_current_user",
                "User not found for the current authentication!"
        ));
    }

}
