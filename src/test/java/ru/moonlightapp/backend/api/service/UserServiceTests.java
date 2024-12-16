package ru.moonlightapp.backend.api.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.moonlightapp.backend.SpringBootTests;
import ru.moonlightapp.backend.exception.ApiException;
import ru.moonlightapp.backend.exception.GenericErrorException;
import ru.moonlightapp.backend.model.attribute.Sex;
import ru.moonlightapp.backend.storage.model.User;
import ru.moonlightapp.backend.storage.repository.UserRepository;
import ru.moonlightapp.backend.util.CharSequenceGenerator;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public final class UserServiceTests extends SpringBootTests {

    @MockBean private UserRepository userRepository;

    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private UserService userService;

    @Test
    void contextLoads() {
        assertNotNull(userRepository);
        assertNotNull(passwordEncoder);
        assertNotNull(userService);
    }

    @Test
    void whenChangePasswordWithWrongCurrent_thenThrowsApiException() {
        String currentPassword = CharSequenceGenerator.generateRandomAlphanumericString(16, true);

        User user = User.builder()
                .withPassword(passwordEncoder.encode(currentPassword))
                .build();

        ApiException exception = assertThrows(
                ApiException.class,
                () -> userService.changePassword(user, "not-" + currentPassword, null)
        );

        assertEquals("wrong_password", exception.getErrorCode());
    }

    @Test
    void whenChangePasswordWithCorrectCurrent_thenSuccess() {
        String currentPassword = CharSequenceGenerator.generateRandomAlphanumericString(16, true);
        String newPassword = CharSequenceGenerator.generateRandomAlphanumericString(16, true) + "-new";

        User user = User.builder()
                .withPassword(passwordEncoder.encode(currentPassword))
                .build();

        assertDoesNotThrow(() -> userService.changePassword(user, currentPassword, newPassword));

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void whenUpdateProfileWithNullsAndNullName_thenThrowsGenericErrorException() {
        assertThrows(GenericErrorException.class, () -> userService.updateProfile(null, null, null, null, true));
    }

    @Test
    void whenUpdateProfileWithoutNullsAndNullFields_thenSuccess() {
        String name = "User";

        User user = User.builder()
                .withName(name)
                .build();

        assertDoesNotThrow(() -> userService.updateProfile(user, name, null, null, false));

        verify(userRepository, never()).save(any(User.class));

        assertEquals(name, user.getName());
        assertNull(user.getBirthDate());
        assertNull(user.getSex());
    }

    @Test
    void whenUpdateProfileWithNullsAndRealFields_thenSuccess() {
        String name = "User";

        User user = User.builder()
                .withName(name)
                .withBirthDate(LocalDate.of(2022, 3, 26))
                .build();

        assertDoesNotThrow(() -> userService.updateProfile(user, name, null, Sex.FEMALE, true));

        verify(userRepository, times(1)).save(any(User.class));

        assertEquals(name, user.getName());
        assertNull(user.getBirthDate());
        assertEquals(Sex.FEMALE, user.getSex());
    }

    @Test
    void whenUpdateProfileWithoutNullsAndRealFields_thenSuccess() {
        String name = "User";

        User user = User.builder()
                .withName(name)
                .withBirthDate(LocalDate.of(2022, 3, 26))
                .build();

        assertDoesNotThrow(() -> userService.updateProfile(user, name, null, Sex.FEMALE, false));

        verify(userRepository, times(1)).save(any(User.class));

        assertEquals(name, user.getName());
        assertEquals(user.getBirthDate(), LocalDate.of(2022, 3, 26));
        assertEquals(Sex.FEMALE, user.getSex());
    }

    @Test
    void whenUpdateProfileWithoutNullsAndSameFields_thenSuccess() {
        String name = "User";

        User user = User.builder()
                .withName(name)
                .withBirthDate(LocalDate.of(2022, 3, 26))
                .withSex(Sex.FEMALE)
                .build();

        assertDoesNotThrow(() -> userService.updateProfile(user, name, LocalDate.of(2022, 3, 26), null, false));

        verify(userRepository, never()).save(any(User.class));

        assertEquals(name, user.getName());
        assertEquals(user.getBirthDate(), LocalDate.of(2022, 3, 26));
        assertEquals(Sex.FEMALE, user.getSex());
    }

    @Test
    void whenUpdateProfileWithNullsAndSameFields_thenSuccess() {
        String name = "User";

        User user = User.builder()
                .withName(name)
                .withBirthDate(LocalDate.of(2022, 3, 26))
                .withSex(Sex.FEMALE)
                .build();

        assertDoesNotThrow(() -> userService.updateProfile(user, name, LocalDate.of(2022, 3, 26), null, true));

        verify(userRepository, times(1)).save(any(User.class));

        assertEquals(name, user.getName());
        assertEquals(user.getBirthDate(), LocalDate.of(2022, 3, 26));
        assertNull(user.getSex());
    }

}
