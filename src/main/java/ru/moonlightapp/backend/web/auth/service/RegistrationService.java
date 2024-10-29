package ru.moonlightapp.backend.web.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.moonlightapp.backend.exception.ApiException;
import ru.moonlightapp.backend.model.attribute.Sex;
import ru.moonlightapp.backend.storage.model.User;
import ru.moonlightapp.backend.storage.repository.UserRepository;
import ru.moonlightapp.backend.web.auth.service.EmailConfirmationService.ProofKeyConsumer;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public final class RegistrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailConfirmationService confirmationService;

    public void registerNewUser(String email, String password, String proofKey, String name, LocalDate birthDate, Sex sex) throws ApiException {
        Optional<User> existing = userRepository.findById(email);
        if (existing.isPresent())
            throw new ApiException("user_already_exists", "An user with same email address is already registered");

        confirmationService.validateEmailConfirmation(email, proofKey);

        String encodedPassword = passwordEncoder.encode(password);
        User user = new User(email, encodedPassword, name, birthDate, sex);

        userRepository.save(user);
        confirmationService.forgetEmailConfirmation(email);

        log.info("Registered a new user '{}'.", email);
    }

    public void processConfirmationRequest(String email, String name, boolean renew, ProofKeyConsumer proofKeyConsumer) throws ApiException {
        if (userRepository.existsById(email))
            throw new ApiException("email_already_used", "This email is already used by other account");

        confirmationService.requestEmailConfirmation(
                email,
                renew,
                "Moonlight - Регистрация аккаунта",
                "Ваш код подтверждения для завершения регистрации аккаунта:",
                name,
                code -> MessageFormat.format("""
                        Hello, dear {0}!
                        Here is your confirmation code:
                        {1}
                        """, name, code),
                proofKeyConsumer
        );
    }

    public void processEmailConfirmation(String email, String code, String proofKey) throws ApiException {
        confirmationService.processEmailConfirmation(email, code, proofKey);
    }

}