package ru.moonlightapp.backend.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import ru.moonlightapp.backend.storage.repository.TokenPairRepository;
import ru.moonlightapp.backend.storage.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public final class JwtTokenServiceTests {

    @Mock private UserRepository userRepository;
    @Mock private TokenPairRepository tokenPairRepository;

    @InjectMocks
    private JwtTokenService jwtTokenService;

    @Test
    void contextLoads() {
        assertNotNull(userRepository);
        assertNotNull(tokenPairRepository);
        assertNotNull(jwtTokenService);
    }

}
