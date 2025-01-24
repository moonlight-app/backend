package ru.moonlightapp.backend.service.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ProviderNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import ru.moonlightapp.backend.core.model.ErrorModel;
import ru.moonlightapp.backend.core.storage.model.TokenPair;
import ru.moonlightapp.backend.service.auth.model.TokenPairModel;
import ru.moonlightapp.backend.service.auth.service.jwt.JwtTokenService;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class MoonlightAuthenticationHandler implements AuthenticationSuccessHandler, AuthenticationFailureHandler {

    private static final String DEFAULT_ERROR_CODE = "unexpected_error";

    private final JwtTokenService jwtTokenService;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        if (authentication instanceof UsernamePasswordAuthenticationToken token) {
            if (token.getPrincipal() instanceof UserDetails userDetails) {
                TokenPair tokenPair = jwtTokenService.createTokenPair(userDetails.getUsername());
                TokenPairModel model = new TokenPairModel(
                        tokenPair.getAccessToken(),
                        jwtTokenService.getJwtAccessTokenLifetime(),
                        tokenPair.getRefreshToken(),
                        jwtTokenService.getJwtRefreshTokenLifetime()
                );

                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                objectMapper.writeValue(response.getOutputStream(), model);
            }
        }

        response.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        String errorCode = DEFAULT_ERROR_CODE;
        String errorMessage = exception.getMessage();

        if (exception instanceof BadCredentialsException || exception instanceof UsernameNotFoundException) {
            errorCode = "bad_credentials";
            errorMessage = "The username or password is incorrect";
        } else if (exception instanceof ProviderNotFoundException) {
            errorCode = "provider_not_found";
            errorMessage = "Provider not found";
        }

        ErrorModel error = new ErrorModel(errorCode, errorMessage);
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), error);
    }

}