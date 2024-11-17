package ru.moonlightapp.backend.web.auth.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.moonlightapp.backend.SpringBootTests;
import ru.moonlightapp.backend.exception.ApiException;
import ru.moonlightapp.backend.model.attribute.Sex;
import ru.moonlightapp.backend.storage.model.User;
import ru.moonlightapp.backend.storage.repository.UserRepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public final class RegistrationServiceTests extends SpringBootTests {

    @MockBean private UserRepository userRepository;
    @MockBean private EmailConfirmationService emailConfirmationService;

    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private RegistrationService registrationService;

    @Test
    void contextLoads() {
        assertNotNull(userRepository);
        assertNotNull(emailConfirmationService);
        assertNotNull(passwordEncoder);
        assertNotNull(registrationService);
    }

    @Test
    void whenRequestWithNotKnownBeforeEmail_thenSuccess() throws ApiException {
        String email = "test@test.com";
        String name = "Name";

        assertDoesNotThrow(() -> registrationService.processConfirmationRequest(email, name, true, proofKey -> {}));

        verify(emailConfirmationService, times(1)).requestEmailConfirmation(
                anyString(), anyBoolean(), anyString(), anyString(), anyString(),
                any(EmailConfirmationService.MailContentFormatter.class),
                any(EmailConfirmationService.ProofKeyConsumer.class)
        );
    }

    @Test
    void whenRequestWithAlreadyUsedEmail_thenThrowsApiException() {
        String email = "test@test.com";
        String name = "Name";

        when(userRepository.existsById(email)).thenReturn(true);

        ApiException exception = assertThrows(
                ApiException.class,
                () -> registrationService.processConfirmationRequest(email, name, true, proofKey -> {})
        );

        assertEquals(exception.getErrorCode(), "email_already_used");
    }

    @Test
    void whenRegisterWithNotKnownBeforeEmail_thenSuccess() throws ApiException {
        String email = "test@test.com";

        assertDoesNotThrow(() -> registrationService.registerNewUser(email, "secret-password", "", "", LocalDate.now(), Sex.MALE));

        verify(emailConfirmationService, times(1)).validateEmailConfirmation(anyString(), anyString());
        verify(userRepository, times(1)).save(any(User.class));
        verify(emailConfirmationService, times(1)).forgetEmailConfirmation(anyString());
    }

    @Test
    void whenRegisterWithAlreadyUsedEmail_thenThrowsApiException() {
        String email = "test@test.com";

        User user = User.builder()
                .withEmail(email)
                .withName("Name")
                .build();

        when(userRepository.findById(email)).thenReturn(Optional.of(user));

        ApiException exception = assertThrows(
                ApiException.class,
                () -> registrationService.registerNewUser(email, "", "", "", LocalDate.now(), Sex.MALE)
        );

        assertEquals(exception.getErrorCode(), "user_already_exists");
    }

}
