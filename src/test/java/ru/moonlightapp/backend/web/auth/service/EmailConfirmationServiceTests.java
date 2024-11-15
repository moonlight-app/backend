package ru.moonlightapp.backend.web.auth.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.moonlightapp.backend.exception.ApiException;
import ru.moonlightapp.backend.service.MailService;
import ru.moonlightapp.backend.storage.model.auth.EmailConfirmation;
import ru.moonlightapp.backend.storage.repository.auth.EmailConfirmationRepository;
import ru.moonlightapp.backend.util.CharSequenceGenerator;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static ru.moonlightapp.backend.web.auth.service.EmailConfirmationService.REQUEST_CAN_BE_RENEWED_IN;
import static ru.moonlightapp.backend.web.auth.service.EmailConfirmationService.REQUEST_WILL_BE_EXPIRED_IN;

@SpringBootTest
public final class EmailConfirmationServiceTests {

    @MockBean private EmailConfirmationRepository emailConfirmationRepository;
    @MockBean private MailService mailService;

    @Autowired private EmailConfirmationService emailConfirmationService;

    @Test
    void contextLoads() {
        assertNotNull(emailConfirmationRepository);
        assertNotNull(mailService);
        assertNotNull(emailConfirmationService);
    }

    @Test
    void whenRequestWithConfirmedEmail_thenThrowsApiException() {
        String email = "test@test.com";

        EmailConfirmation confirmation = EmailConfirmation.builder()
                .withEmail(email)
                .withConfirmed(true)
                .build();

        when(emailConfirmationRepository.findById(email)).thenReturn(Optional.of(confirmation));

        ApiException exception = assertThrows(
                ApiException.class,
                () -> emailConfirmationService.requestEmailConfirmation(
                    email, false, "title", "subtitle", "username", code -> code, proofKey -> {}
                )
        );

        assertEquals(exception.getErrorCode(), "email_already_confirmed");
    }

    @Test
    void whenRequestWithUnrenewableConfirmation_thenThrowsApiException() {
        String email = "test@test.com";

        EmailConfirmation confirmation = EmailConfirmation.builder()
                .withEmail(email)
                .withRequestedAt(Instant.now().minusSeconds(REQUEST_CAN_BE_RENEWED_IN + 1))
                .build();

        when(emailConfirmationRepository.findById(email)).thenReturn(Optional.of(confirmation));

        ApiException exception = assertThrows(
                ApiException.class,
                () -> emailConfirmationService.requestEmailConfirmation(
                    email, true, "title", "subtitle", "username", code -> code, proofKey -> {}
                )
        );

        assertEquals(exception.getErrorCode(), "email_confirmation_unrenewable");
    }

    @Test
    void whenRequestWithNonExpiredConfirmation_thenThrowsApiException() {
        String email = "test@test.com";

        EmailConfirmation confirmation = EmailConfirmation.builder()
                .withEmail(email)
                .withRequestedAt(Instant.now())
                .build();

        when(emailConfirmationRepository.findById(email)).thenReturn(Optional.of(confirmation));

        ApiException exception = assertThrows(
                ApiException.class,
                () -> emailConfirmationService.requestEmailConfirmation(
                        email, false, "title", "subtitle", "username", code -> code, proofKey -> {}
                )
        );

        assertEquals(exception.getErrorCode(), "email_confirmation_pending");
    }

    @Test
    void whenRequestWithRenewableConfirmation_thenShouldBeRenewed() {
        String email = "test@test.com";

        EmailConfirmation confirmation = EmailConfirmation.builder()
                .withEmail(email)
                .withRequestedAt(Instant.now())
                .build();

        when(emailConfirmationRepository.findById(email)).thenReturn(Optional.of(confirmation));

        assertDoesNotThrow(() -> emailConfirmationService.requestEmailConfirmation(
                email, true, "title", "subtitle", "username", code -> code, proofKey -> {}
        ));

        verify(mailService, times(1)).sendMailAsync(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void whenRequestWithNotKnownBeforeEmail_thenSuccess() {
        String email = "test@test.com";

        assertDoesNotThrow(() -> emailConfirmationService.requestEmailConfirmation(
                email, false, "title", "subtitle", "username", code -> code, proofKey -> {}
        ));

        verify(mailService, times(1)).sendMailAsync(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void whenConfirmWithNotKnownBeforeEmail_thenThrowsApiException() {
        String email = "test@test.com";

        ApiException exception = assertThrows(
                ApiException.class,
                () -> emailConfirmationService.processEmailConfirmation(email, null, null)
        );

        assertEquals(exception.getErrorCode(), "request_not_found");
    }

    @Test
    void whenConfirmWithExpiredConfirmation_thenThrowsApiException() {
        String email = "test@test.com";

        EmailConfirmation confirmation = EmailConfirmation.builder()
                .withEmail(email)
                .withRequestedAt(Instant.now().minusSeconds(REQUEST_WILL_BE_EXPIRED_IN + 1))
                .build();

        when(emailConfirmationRepository.findById(email)).thenReturn(Optional.of(confirmation));

        ApiException exception = assertThrows(
                ApiException.class,
                () -> emailConfirmationService.processEmailConfirmation(email, null, null)
        );

        assertEquals(exception.getErrorCode(), "request_expired");
    }

    @Test
    void whenConfirmWithWrongProofKey_thenThrowsApiException() {
        String email = "test@test.com";
        String proofKey = CharSequenceGenerator.generateProofKey();

        EmailConfirmation confirmation = EmailConfirmation.builder()
                .withEmail(email)
                .withProofKey(proofKey)
                .withRequestedAt(Instant.now())
                .build();

        when(emailConfirmationRepository.findById(email)).thenReturn(Optional.of(confirmation));

        ApiException exception = assertThrows(
                ApiException.class,
                () -> emailConfirmationService.processEmailConfirmation(email, null, "not-" + proofKey)
        );

        assertEquals(exception.getErrorCode(), "wrong_proof_key");
    }

    @Test
    void whenConfirmWithNoMoreAttempts_thenThrowsApiException() {
        String email = "test@test.com";
        String proofKey = CharSequenceGenerator.generateProofKey();

        EmailConfirmation confirmation = EmailConfirmation.builder()
                .withEmail(email)
                .withProofKey(proofKey)
                .withAttemptsLeft(0)
                .withRequestedAt(Instant.now())
                .build();

        when(emailConfirmationRepository.findById(email)).thenReturn(Optional.of(confirmation));

        ApiException exception = assertThrows(
                ApiException.class,
                () -> emailConfirmationService.processEmailConfirmation(email, null, proofKey)
        );

        assertEquals(exception.getErrorCode(), "no_more_attempts");
    }

    @Test
    void whenConfirmWithWrongCode_thenThrowsApiException() {
        String email = "test@test.com";
        String code = CharSequenceGenerator.generateRandomDigitsCode(6);
        String proofKey = CharSequenceGenerator.generateProofKey();

        EmailConfirmation confirmation = new EmailConfirmation(email);
        confirmation.renew(code, proofKey);

        when(emailConfirmationRepository.findById(email)).thenReturn(Optional.of(confirmation));

        ApiException exception = assertThrows(
                ApiException.class,
                () -> emailConfirmationService.processEmailConfirmation(email, "not-" + code, proofKey)
        );

        assertEquals(exception.getErrorCode(), "wrong_code");
    }

    @Test
    void whenConfirmWithCorrectCode_thenSuccess() {
        String email = "test@test.com";
        String code = CharSequenceGenerator.generateRandomDigitsCode(6);
        String proofKey = CharSequenceGenerator.generateProofKey();

        EmailConfirmation confirmation = new EmailConfirmation(email);
        confirmation.renew(code, proofKey);

        when(emailConfirmationRepository.findById(email)).thenReturn(Optional.of(confirmation));

        assertDoesNotThrow(() -> emailConfirmationService.processEmailConfirmation(email, code, proofKey));

        verify(emailConfirmationRepository, times(1)).save(any(EmailConfirmation.class));
    }

    @Test
    void whenValidateNotKnownBeforeEmail_thenThrowsApiException() {
        String email = "test@test.com";

        ApiException exception = assertThrows(
                ApiException.class,
                () -> emailConfirmationService.validateEmailConfirmation(email, null)
        );

        assertEquals(exception.getErrorCode(), "email_not_confirmed");
    }

    @Test
    void whenValidateNotConfirmedEmail_thenThrowsApiException() {
        String email = "test@test.com";

        EmailConfirmation confirmation = new EmailConfirmation(email);

        when(emailConfirmationRepository.findById(email)).thenReturn(Optional.of(confirmation));

        ApiException exception = assertThrows(
                ApiException.class,
                () -> emailConfirmationService.validateEmailConfirmation(email, null)
        );

        assertEquals(exception.getErrorCode(), "email_not_confirmed");
    }

    @Test
    void whenValidateWithWrongProofKey_thenThrowsApiException() {
        String email = "test@test.com";
        String proofKey = CharSequenceGenerator.generateProofKey();

        EmailConfirmation confirmation = EmailConfirmation.builder()
                .withEmail(email)
                .withProofKey(proofKey)
                .withConfirmed(true)
                .build();

        when(emailConfirmationRepository.findById(email)).thenReturn(Optional.of(confirmation));

        ApiException exception = assertThrows(
                ApiException.class,
                () -> emailConfirmationService.validateEmailConfirmation(email, "not-" + proofKey)
        );

        assertEquals(exception.getErrorCode(), "wrong_proof_key");
    }

    @Test
    void whenValidateWithCorrectProofKey_thenSuccess() {
        String email = "test@test.com";
        String proofKey = CharSequenceGenerator.generateProofKey();

        EmailConfirmation confirmation = EmailConfirmation.builder()
                .withEmail(email)
                .withProofKey(proofKey)
                .withConfirmed(true)
                .build();

        when(emailConfirmationRepository.findById(email)).thenReturn(Optional.of(confirmation));

        assertDoesNotThrow(() -> emailConfirmationService.validateEmailConfirmation(email, proofKey));
    }

    @Test
    void whenForgetConfirmation_thenSuccess() {
        String email = "test@test.com";

        emailConfirmationService.forgetEmailConfirmation(email);

        verify(emailConfirmationRepository, times(1)).deleteById(anyString());
    }

}
