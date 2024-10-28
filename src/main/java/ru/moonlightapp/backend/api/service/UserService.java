package ru.moonlightapp.backend.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.moonlightapp.backend.exception.ApiException;
import ru.moonlightapp.backend.exception.GenericErrorException;
import ru.moonlightapp.backend.model.Sex;
import ru.moonlightapp.backend.storage.model.User;
import ru.moonlightapp.backend.storage.repository.UserRepository;

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

    public boolean isUserExist(String email) {
        return userRepository.existsById(email);
    }

    public void changeName(User user, String name) {
        if (user.changeName(name)) {
            userRepository.save(user);
        }
    }

    public void changeBirthDate(User user, LocalDate birthDate) {
        if (user.changeBirthDate(birthDate)) {
            userRepository.save(user);
        }
    }

    public void changeSex(User user, Sex sex) {
        if (user.changeSex(sex)) {
            userRepository.save(user);
        }
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

    public void deleteUser(User user) {
        userRepository.delete(user);
    }

}