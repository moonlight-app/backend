package ru.moonlightapp.backend.web.auth.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import ru.moonlightapp.backend.exception.ApiException;
import ru.moonlightapp.backend.service.MailService;
import ru.moonlightapp.backend.storage.model.auth.EmailConfirmation;
import ru.moonlightapp.backend.storage.repository.auth.EmailConfirmationRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
public final class EmailConfirmationServiceTests {

    @Mock private JavaMailSender javaMailSender;
    @Mock private EmailConfirmationRepository emailConfirmationRepository;

    @InjectMocks private MailService mailService;
    @InjectMocks private EmailConfirmationService emailConfirmationService;

    @Test
    void contextLoads() {
        assertNotNull(javaMailSender);
        assertNotNull(emailConfirmationRepository);
        assertNotNull(mailService);
        assertNotNull(emailConfirmationService);
    }

    @Test
    void whenEmailIsConfirmed_thenThrowsApiException() {
        String email = "test@test.com";

        EmailConfirmation confirmation = EmailConfirmation.builder()
                .withEmail(email)
                .withConfirmed(true)
                .build();

        when(emailConfirmationRepository.findById(email)).thenReturn(Optional.of(confirmation));

        ApiException exception = assertThrows(ApiException.class, () -> emailConfirmationService.requestEmailConfirmation(
                email, false, "title", "subtitle", "username", code -> code, proofKey -> {})
        );

        assertEquals(exception.getErrorCode(), "email_already_confirmed");
    }

}
