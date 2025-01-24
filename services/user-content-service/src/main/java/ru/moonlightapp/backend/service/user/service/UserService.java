package ru.moonlightapp.backend.service.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.moonlightapp.backend.core.exception.ApiException;
import ru.moonlightapp.backend.core.exception.GenericErrorException;
import ru.moonlightapp.backend.core.model.attribute.Sex;
import ru.moonlightapp.backend.core.storage.model.User;
import ru.moonlightapp.backend.core.storage.repository.UserRepository;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public final class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public Optional<User> findByEmail(String email) {
        return userRepository.findById(email);
    }

    public void changePassword(User user, String currentPassword, String newPassword) throws ApiException {
        if (!passwordEncoder.matches(currentPassword, user.getPassword()))
            throw new ApiException("wrong_password", "Password isn't correct");

        if (user.changePassword(passwordEncoder.encode(newPassword))) {
            userRepository.save(user);
        }
    }

    public void updateProfile(User user, String name, LocalDate birthDate, Sex sex, boolean writeNulls) throws GenericErrorException {
        if (name == null && writeNulls)
            throw GenericErrorException.fromConstraintViolation("name", "cannot be null");

        boolean updated = false;

        if (name != null)
            updated = user.changeName(name) | updated;

        if (birthDate != null || writeNulls)
            updated = user.changeBirthDate(birthDate) | updated;

        if (sex != null || writeNulls)
            updated = user.changeSex(sex) | updated;

        if (updated) {
            userRepository.save(user);
        }
    }

    public void deleteProfile(String userEmail) {
        userRepository.deleteById(userEmail);
        SecurityContextHolder.getContext().setAuthentication(null);
    }

}