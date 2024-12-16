package ru.moonlightapp.backend.web.auth.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.moonlightapp.backend.SpringBootTests;
import ru.moonlightapp.backend.exception.ApiException;
import ru.moonlightapp.backend.storage.model.User;
import ru.moonlightapp.backend.storage.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public final class RecoveryServiceTests extends SpringBootTests {

    @MockBean private UserRepository userRepository;
    @MockBean private EmailConfirmationService emailConfirmationService;

    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private RecoveryService recoveryService;

    @Test
    void contextLoads() {
        assertNotNull(userRepository);
        assertNotNull(emailConfirmationService);
        assertNotNull(passwordEncoder);
        assertNotNull(recoveryService);
    }

    @Test
    void whenRequestUnknownUser_thenThrowsApiException() {
        String email = "test@test.com";

        ApiException exception = assertThrows(
                ApiException.class,
                () -> recoveryService.processConfirmationRequest(email, true, proofKey -> {})
        );

        assertEquals("user_not_found", exception.getErrorCode());
    }

    @Test
    void whenRequestKnownUser_thenSuccess() throws ApiException {
        String email = "test@test.com";

        User user = User.builder()
                .withEmail(email)
                .withName("Name")
                .build();

        when(userRepository.findById(email)).thenReturn(Optional.of(user));

        assertDoesNotThrow(() -> recoveryService.processConfirmationRequest(email, true, proofKey -> {}));

        verify(emailConfirmationService, times(1)).requestEmailConfirmation(
                anyString(), anyBoolean(), anyString(), anyString(), anyString(),
                any(EmailConfirmationService.MailContentFormatter.class),
                any(EmailConfirmationService.ProofKeyConsumer.class)
        );
    }

    @Test
    void whenConfirmWithNotKnownBeforeEmail_thenThrowsApiException() {
        String email = "test@test.com";

        ApiException exception = assertThrows(
                ApiException.class,
                () -> recoveryService.processEmailConfirmation(email, "", "")
        );

        assertEquals("user_not_found", exception.getErrorCode());
    }

    @Test
    void whenConfirmWithKnownEmail_thenSuccess() throws ApiException {
        String email = "test@test.com";
        when(userRepository.existsById(email)).thenReturn(true);

        assertDoesNotThrow(() -> recoveryService.processEmailConfirmation(email, "", ""));

        verify(emailConfirmationService, times(1)).processEmailConfirmation(anyString(), anyString(), anyString());
    }

    @Test
    void whenPerformWithNotKnownBeforeEmail_thenThrowsApiException() {
        String email = "test@test.com";

        ApiException exception = assertThrows(
                ApiException.class,
                () -> recoveryService.performRecovery(email, "", "")
        );

        assertEquals("user_not_found", exception.getErrorCode());
    }

    @Test
    void whenPerformWithKnownEmail_thenSuccess() throws ApiException {
        String email = "test@test.com";

        User user = User.builder()
                .withEmail(email)
                .withName("Name")
                .build();

        when(userRepository.findById(email)).thenReturn(Optional.of(user));

        assertDoesNotThrow(() -> recoveryService.performRecovery(email, "secret-password", ""));

        verify(emailConfirmationService, times(1)).validateEmailConfirmation(anyString(), anyString());
        verify(userRepository, times(1)).save(any(User.class));
        verify(emailConfirmationService, times(1)).forgetEmailConfirmation(anyString());
    }

}
