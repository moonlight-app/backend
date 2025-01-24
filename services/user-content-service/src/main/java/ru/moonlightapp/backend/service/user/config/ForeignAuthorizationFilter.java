package ru.moonlightapp.backend.service.user.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestClient;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.moonlightapp.backend.core.storage.model.User;
import ru.moonlightapp.backend.core.storage.repository.UserRepository;

import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
public final class ForeignAuthorizationFilter extends OncePerRequestFilter {

    private final DiscoveryClient discoveryClient;
    private final RestClient restClient;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Optional<String> foundAccessToken = tryFetchBearerToken(request);
        if (foundAccessToken.isPresent()) {
            ServiceInstance instance = discoveryClient.getInstances("auth-server").getFirst();
            ResponseEntity<String> userEmail = restClient.get()
                    .uri(instance.getUri() + "/auth/with-token")
                    .header("Access-Token", foundAccessToken.get())
                    .retrieve()
                    .toEntity(String.class);

            User user = userRepository.findById(userEmail.getBody()).orElseThrow();
            UsernamePasswordAuthenticationToken authentication = UsernamePasswordAuthenticationToken.authenticated(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private static Optional<String> tryFetchBearerToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        return authorization != null && authorization.startsWith("Bearer ")
                ? Optional.of(authorization.substring(7))
                : Optional.empty();
    }

}
