package ru.moonlightapp.backend.web.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.moonlightapp.backend.exception.ApiException;
import ru.moonlightapp.backend.service.MailService;
import ru.moonlightapp.backend.storage.model.auth.EmailConfirmation;
import ru.moonlightapp.backend.storage.repository.auth.EmailConfirmationRepository;
import ru.moonlightapp.backend.util.CharSequenceGenerator;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.apache.commons.io.IOUtils.closeQuietly;

@Slf4j
@Service
@RequiredArgsConstructor
public final class EmailConfirmationService {

    public static final int DEFAULT_CONFIRM_ATTEMPTS = 5;
    public static final long REQUEST_WILL_BE_EXPIRED_IN = 300L;
    public static final long REQUEST_CAN_BE_RENEWED_IN = 180L;

    private final EmailConfirmationRepository emailConfirmationRepository;
    private final MailService mailService;

    public void requestEmailConfirmation(
            String email,
            boolean renew,
            String mailTitle,
            String mailSubtitle,
            String userName,
            MailContentFormatter mailContentFormatter,
            ProofKeyConsumer proofKeyConsumer
    ) throws ApiException {
        Optional<EmailConfirmation> existing = emailConfirmationRepository.findById(email);
        if (existing.isPresent()) {
            if (existing.get().isConfirmed())
                throw new ApiException("email_already_confirmed", "This email is already confirmed");

            if (renew) {
                if (!existing.get().canBeRenewed()) {
                    throw new ApiException(
                            "email_confirmation_unrenewable",
                            "This email already has a pending confirmation request that cannot be renewed yet"
                    );
                }
            } else {
                if (!existing.get().isExpired()) {
                    throw new ApiException(
                            "email_confirmation_pending",
                            "This email already has a pending confirmation request"
                    );
                }
            }
        }

        String code = CharSequenceGenerator.generateRandomAlphanumericString(6, false);

        InputStream resource = getClass().getResourceAsStream("/templates/mail/confirmation.html");
        BufferedReader reader = new BufferedReader(new InputStreamReader(resource, StandardCharsets.UTF_8));
        String html = reader.lines().map(String::trim).collect(Collectors.joining("\n"));
        closeQuietly(reader);

        html = html.replace("{{code}}", code.toUpperCase())
                .replace("{{user}}", userName)
                .replace("{{subtitle}}", mailSubtitle);

        mailService.sendMailAsync(email, mailTitle, mailContentFormatter.formatContent(code.toUpperCase()), html);

        String proofKey = CharSequenceGenerator.generateProofKey();
        EmailConfirmation confirmation = existing.orElseGet(() -> new EmailConfirmation(email));
        confirmation.renew(code, proofKey);
        emailConfirmationRepository.save(confirmation);

        proofKeyConsumer.accept(proofKey);
        log.info("Requested email confirmation for '{}'.", email);
    }

    public void processEmailConfirmation(String email, String code, String proofKey) throws ApiException {
        Optional<EmailConfirmation> confirmation = emailConfirmationRepository.findById(email);
        if (confirmation.isEmpty())
            throw new ApiException("request_not_found", "A confirmation wasn't requested for this email");

        if (confirmation.get().isExpired())
            throw new ApiException("request_expired", "The confirmation request is expired");

        if (!confirmation.get().getProofKey().equalsIgnoreCase(proofKey))
            throw new ApiException("wrong_proof_key", "Your proof key isn't correct");

        if (confirmation.get().hasNoMoreAttempts())
            throw new ApiException("no_more_attempts", "You have no more attempts to confirm this email");

        try {
            String correctCode = confirmation.get().getCode();
            if (correctCode != null && correctCode.equalsIgnoreCase(code)) {
                confirmation.get().setConfirmed(true);
                log.info("Email '{}' has been confirmed.", email);
            } else {
                confirmation.get().decreaseAttemptsCounter();

                int attemptsLeft = confirmation.get().getAttemptsLeft();
                log.info("Email confirmation failed for '{}' (wrong code, attempts left: {})", email, attemptsLeft);
                throw new ApiException("wrong_code", "Your code doesn't match with the correct", attemptsLeft);
            }
        } finally {
            emailConfirmationRepository.save(confirmation.get());
        }
    }

    public void validateEmailConfirmation(String email, String proofKey) throws ApiException {
        Optional<EmailConfirmation> confirmation = emailConfirmationRepository.findById(email);
        if (confirmation.isEmpty() || !confirmation.get().isConfirmed())
            throw new ApiException("email_not_confirmed", "This operation requires an email confirmation");

        if (confirmation.get().getProofKey().equals(proofKey))
            return;

        throw new ApiException("wrong_proof_key", "Your proof key isn't correct");
    }

    public void forgetEmailConfirmation(String email) {
        emailConfirmationRepository.deleteById(email);
    }

    public static String extractProofKey(HttpServletRequest request) throws ApiException {
        String proofKey = request.getHeader(ProofKeyConsumer.PROOF_KEY_HTTP_HEADER_NAME);
        if (proofKey != null)
            return proofKey;

        throw new ApiException("wrong_proof_key", "Your proof key isn't correct");
    }

    @FunctionalInterface
    public interface MailContentFormatter {

        String formatContent(String code);

    }

    @FunctionalInterface
    public interface ProofKeyConsumer {

        String PROOF_KEY_HTTP_HEADER_NAME = "X-Proof-Key";

        static ProofKeyConsumer httpHeaderBased(HttpServletResponse response) {
            return proofKey -> response.addHeader(PROOF_KEY_HTTP_HEADER_NAME, proofKey);
        }

        void accept(String proofKey);

    }

}