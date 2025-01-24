package ru.moonlightapp.backend.service.auth.service.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.moonlightapp.backend.core.exception.ApiException;
import ru.moonlightapp.backend.core.model.ErrorModel;
import ru.moonlightapp.backend.core.storage.model.User;
import ru.moonlightapp.backend.core.storage.repository.UserRepository;
import ru.moonlightapp.backend.service.auth.MoonlightAuthenticationDetailsSource;

import java.io.IOException;
import java.util.Optional;

import static ru.moonlightapp.backend.service.auth.service.jwt.JwtTokenService.tryFetchBearerToken;

@RequiredArgsConstructor
public final class JwtTokenAuthorizationFilter extends OncePerRequestFilter {

    private static final String TOKEN_REFRESH_ENDPOINT = "/auth/token/refresh";

    private final JwtTokenService jwtTokenService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (TOKEN_REFRESH_ENDPOINT.equals(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        Optional<String> foundAccessToken = tryFetchBearerToken(request);
        if (foundAccessToken.isPresent()) {
            String accessToken = foundAccessToken.get();
            if (!accessToken.isEmpty()) {
                try {
                    jwtTokenService.validateAccessToken(accessToken);
                } catch (ApiException ex) {
                    response.setStatus(ex.getStatusCode().value());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    objectMapper.writeValue(response.getOutputStream(), ex.constructModel());
                    return;
                }
            }

            Optional<User> foundUser = jwtTokenService.extractUserEmail(accessToken).flatMap(userRepository::findById);
            if (foundUser.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                objectMapper.writeValue(
                        response.getOutputStream(),
                        new ErrorModel("user_not_found", "The token owner doesn't exist!"
                ));
                return;
            }

            User user = foundUser.get();
            UsernamePasswordAuthenticationToken authentication = UsernamePasswordAuthenticationToken.authenticated(user, null, user.getAuthorities());
            authentication.setDetails(MoonlightAuthenticationDetailsSource.SINGLETON.buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

}
