package ru.moonlightapp.backend.web.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.moonlightapp.backend.exception.ApiException;
import ru.moonlightapp.backend.storage.model.User;
import ru.moonlightapp.backend.storage.repository.UserRepository;

import java.text.MessageFormat;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public final class RecoveryService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailConfirmationService confirmationService;

    public void performRecovery(String email, String password, String proofKey) throws ApiException {
        Optional<User> user = userRepository.findById(email);
        if (user.isEmpty())
            throw new ApiException("user_not_found", "User not found");

        confirmationService.validateEmailConfirmation(email, proofKey);

        String encodedPassword = passwordEncoder.encode(password);
        user.get().changePassword(encodedPassword);

        userRepository.save(user.get());
        confirmationService.forgetEmailConfirmation(email);

        log.info("A password has been changed for user '{}'.", email);
    }

    public void processConfirmationRequest(String email, boolean renew, EmailConfirmationService.ProofKeyConsumer proofKeyConsumer) throws ApiException {
        Optional<User> user = userRepository.findById(email);
        if (user.isEmpty())
            throw new ApiException("user_not_found", "User not found");

        String userName = user.get().getName();
        confirmationService.requestEmailConfirmation(
                email,
                renew,
                "Moonlight - Восстановление аккаунта",
                "Ваш код подтверждения для восстановления доступа к аккаунту:",
                userName,
                code -> MessageFormat.format("""
                        Hello, dear {0}!
                        Here is your recovery code:
                        {1}
                        """, userName, code),
                proofKeyConsumer
        );
    }

    public void processEmailConfirmation(String email, String code, String proofKey) throws ApiException {
        Optional<User> user = userRepository.findById(email);
        if (user.isEmpty())
            throw new ApiException("user_not_found", "User not found");

        confirmationService.processEmailConfirmation(email, code, proofKey);
    }

}