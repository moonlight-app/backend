package ru.moonlightapp.backend.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.moonlightapp.backend.SpringBootTests;
import ru.moonlightapp.backend.exception.ApiException;
import ru.moonlightapp.backend.storage.model.TokenPair;
import ru.moonlightapp.backend.storage.model.TokenPairId;
import ru.moonlightapp.backend.storage.repository.TokenPairRepository;
import ru.moonlightapp.backend.storage.repository.UserRepository;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public final class JwtTokenServiceTests extends SpringBootTests {

    @MockBean private TokenPairRepository tokenPairRepository;
    @MockBean private UserRepository userRepository;

    @Autowired private JwtTokenService jwtTokenService;

    @Test
    void contextLoads() {
        assertNotNull(tokenPairRepository);
        assertNotNull(userRepository);
        assertNotNull(jwtTokenService);
    }

    @Test
    void whenCreateTokenPair_shouldReturnValidPair() {
        String email = "test@test.com";
        TokenPair tokenPair = jwtTokenService.createTokenPair(email);
        validateTokenPair(tokenPair);
    }

    @Test
    void whenRefreshUnknownTokenPair_thenThrowsApiException() {
        ApiException exception = assertThrows(
                ApiException.class,
                () -> jwtTokenService.refreshTokenPair("no token", "no token")
        );

        assertEquals("token_pair_not_found", exception.getErrorCode());
    }

    @Test
    void whenRefreshExpiredTokenPair_thenThrowsApiException() {
        String accessToken = "access_token";
        String refreshToken = "refresh_token";

        TokenPair tokenPair = TokenPair.builder()
                .withAccessToken(accessToken)
                .withRefreshToken(refreshToken)
                .withRefreshTokenExpiresAt(Instant.now().minusSeconds(1))
                .build();

        when(tokenPairRepository.findById(new TokenPairId(accessToken, refreshToken))).thenReturn(Optional.of(tokenPair));

        ApiException exception = assertThrows(
                ApiException.class,
                () -> jwtTokenService.refreshTokenPair(accessToken, refreshToken)
        );

        assertEquals("token_is_expired", exception.getErrorCode());
    }

    @Test
    void whenRefreshOwnerlessTokenPair_thenThrowsApiException() {
        String email = "test@test.com";
        when(userRepository.existsById(email)).thenReturn(false);

        TokenPair tokenPair = jwtTokenService.createTokenPair(email);
        String accessToken = tokenPair.getAccessToken();
        String refreshToken = tokenPair.getRefreshToken();
        when(tokenPairRepository.findById(new TokenPairId(accessToken, refreshToken))).thenReturn(Optional.of(tokenPair));

        ApiException exception = assertThrows(
                ApiException.class,
                () -> jwtTokenService.refreshTokenPair(accessToken, refreshToken)
        );

        assertEquals("user_not_found", exception.getErrorCode());
    }

    @Test
    void whenRefreshValidTokenPair_thenSuccess() {
        String email = "test@test.com";
        when(userRepository.existsById(email)).thenReturn(true);

        TokenPair tokenPair = jwtTokenService.createTokenPair(email);
        String accessToken = tokenPair.getAccessToken();
        String refreshToken = tokenPair.getRefreshToken();
        when(tokenPairRepository.findById(new TokenPairId(accessToken, refreshToken))).thenReturn(Optional.of(tokenPair));

        assertDoesNotThrow(() -> jwtTokenService.refreshTokenPair(accessToken, refreshToken));

        verify(tokenPairRepository, atLeastOnce()).save(any(TokenPair.class));
    }

    @Test
    void whenValidateEmptyToken_thenThrowsApiException() {
        ApiException exception = assertThrows(
                ApiException.class,
                () -> jwtTokenService.validateAccessToken("")
        );

        assertEquals("access_denied", exception.getErrorCode());
    }

    @Test
    void whenValidateExpiredToken_thenThrowsApiException() {
        String email = "test@test.com";
        String accessToken = jwtTokenService.generateJwtAccessToken(email, Instant.now().minusSeconds(1));

        ApiException exception = assertThrows(
                ApiException.class,
                () -> jwtTokenService.validateAccessToken(accessToken)
        );

        assertEquals("token_is_expired", exception.getErrorCode());
    }

    @Test
    void whenValidateExpiredTokenPair_thenThrowsApiException() {
        String email = "test@test.com";
        String accessToken = jwtTokenService.generateJwtAccessToken(email, Instant.now().plusSeconds(60L));

        TokenPair tokenPair = TokenPair.builder()
                .withAccessToken(accessToken)
                .withAccessTokenExpiresAt(Instant.now().minusSeconds(1L))
                .withUserEmail(email)
                .build();

        when(tokenPairRepository.getByAccessTokenAndUserEmail(accessToken, email)).thenReturn(tokenPair);

        ApiException exception = assertThrows(
                ApiException.class,
                () -> jwtTokenService.validateAccessToken(accessToken)
        );

        assertEquals("token_is_expired", exception.getErrorCode());
    }

    @Test
    void whenValidateCorrectToken_thenSuccess() {
        String email = "test@test.com";
        String accessToken = jwtTokenService.generateJwtAccessToken(email, Instant.now().plusSeconds(60L));

        TokenPair tokenPair = TokenPair.builder()
                .withAccessToken(accessToken)
                .withAccessTokenExpiresAt(Instant.now().plusSeconds(60L))
                .withUserEmail(email)
                .build();

        when(tokenPairRepository.getByAccessTokenAndUserEmail(accessToken, email)).thenReturn(tokenPair);

        assertDoesNotThrow(() -> jwtTokenService.validateAccessToken(accessToken));
    }

    private static void validateTokenPair(TokenPair tokenPair) {
        assertNotNull(tokenPair);

        assertNotNull(tokenPair.getAccessToken());
        assertFalse(tokenPair.getAccessToken().isEmpty());

        assertNotNull(tokenPair.getAccessTokenExpiresAt());
        assertFalse(tokenPair.isAccessTokenExpired());

        assertNotNull(tokenPair.getRefreshToken());
        assertFalse(tokenPair.getRefreshToken().isEmpty());

        assertNotNull(tokenPair.getRefreshTokenExpiresAt());
        assertFalse(tokenPair.isRefreshTokenExpired());
    }

}
