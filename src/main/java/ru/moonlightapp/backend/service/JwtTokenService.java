package ru.moonlightapp.backend.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.MacAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.moonlightapp.backend.exception.ApiException;
import ru.moonlightapp.backend.storage.model.TokenPair;
import ru.moonlightapp.backend.storage.repository.TokenPairRepository;
import ru.moonlightapp.backend.storage.repository.UserRepository;
import ru.moonlightapp.backend.util.CharSequenceGenerator;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
public final class JwtTokenService {

    private final UserRepository userRepository;
    private final TokenPairRepository tokenPairRepository;

    @Value("${server.jwt.secret}")
    private String jwtSecret;
    @Getter @Value("${server.jwt.lifetime.access-token}")
    private long jwtAccessTokenLifetime;
    @Getter @Value("${server.jwt.lifetime.refresh-token}")
    private long jwtRefreshTokenLifetime;

    public TokenPair createTokenPair(String userEmail) {
        Instant[] expirations = generateExpirations();
        return writeUniqueTokenPair(() -> {
            String accessToken = generateJwtAccessToken(userEmail, expirations[0]);
            String refreshToken = generateJwtRefreshToken(userEmail, expirations[1]);
            return new TokenPair(userEmail, accessToken, expirations[0], refreshToken, expirations[1]);
        });
    }

    public TokenPair refreshTokenPair(String oldAccessToken, String oldRefreshToken) throws ApiException {
        TokenPair foundPair = tokenPairRepository.getByBothTokens(oldAccessToken, oldRefreshToken);
        if (foundPair == null)
            throw new ApiException("token_pair_not_found", "A pair of these tokens not found!");

        if (foundPair.isRefreshTokenExpired())
            throw new ApiException("token_is_expired", "The refresh token is expired!");

        String userEmail = foundPair.getUserEmail();
        if (!userRepository.existsById(userEmail))
            throw new ApiException("user_not_found", "The token pair owner doesn't exist!");

        Instant[] expirations = generateExpirations();
        return writeUniqueTokenPair(() -> {
            foundPair.updateAccessToken(this::generateJwtAccessToken, expirations[0]);
            foundPair.updateRefreshToken(this::generateJwtRefreshToken, expirations[1]);
            return foundPair;
        });
    }

    public void validateAccessToken(String accessToken) throws ApiException {
        if (accessToken != null && !accessToken.isEmpty()) {
            try {
                Jws<Claims> claimsJws = Jwts.parser().verifyWith(key()).build().parseSignedClaims(accessToken);
                Claims payload = claimsJws.getPayload();

                Instant expiration = payload.getExpiration().toInstant();
                if (expiration.isBefore(Instant.now()))
                    throw new ApiException(HttpStatus.FORBIDDEN, "token_is_expired", "Your access token is expired!");

                String userEmail = payload.getSubject();
                if (userEmail != null && !userEmail.isEmpty()) {
                    TokenPair foundPair = tokenPairRepository.getByAccessTokenAndUserEmail(accessToken, userEmail);
                    if (foundPair != null) {
                        if (foundPair.isAccessTokenExpired()) {
                            throw new ApiException(HttpStatus.FORBIDDEN, "token_is_expired", "Your access token is expired!");
                        } else {
                            return;
                        }
                    }
                }
            } catch (ApiException ex) {
                throw ex;
            } catch (Exception ex) {
                log.warn("Invalid JWT token: {} ({})", ex.getMessage(), ex.getClass().getName());
            }
        }

        throw new ApiException(HttpStatus.UNAUTHORIZED, "access_denied", "Invalid access token!");
    }

    public Optional<String> extractUserEmail(String accessToken) {
        try {
            Jws<Claims> claimsJws = Jwts.parser().verifyWith(key()).build().parseSignedClaims(accessToken);
            Claims payload = claimsJws.getPayload();
            return Optional.ofNullable(payload.getSubject());
        } catch (Exception ignored) {
            return Optional.empty();
        }
    }

    private TokenPair writeUniqueTokenPair(Supplier<TokenPair> tokenPairGenerator) {
        while (true) {
            try {
                TokenPair tokenPair = tokenPairGenerator.get();
                tokenPairRepository.save(tokenPair);
                return tokenPair;
            } catch (DataIntegrityViolationException ignored) {
            }
        }
    }

    private String generateJwtAccessToken(String username, Instant expiration) {
        return generateJwtToken(username, expiration, Jwts.SIG.HS256, 16);
    }

    private String generateJwtRefreshToken(String username, Instant expiration) {
        return generateJwtToken(username, expiration, Jwts.SIG.HS512, 128);
    }

    private String generateJwtToken(String username, Instant expiration, MacAlgorithm algorithm, int saltLength) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(expiration))
                .claim("slt", CharSequenceGenerator.generateRandomAlphabeticCode(saltLength, true))
                .signWith(key(), algorithm)
                .compact();
    }

    private Instant[] generateExpirations() {
        Instant now = Instant.now();
        return new Instant[] {
                now.plusSeconds(jwtAccessTokenLifetime),
                now.plusSeconds(jwtRefreshTokenLifetime)
        };
    }

    private SecretKey key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public static Optional<String> tryFetchBearerToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        return authorization != null && authorization.startsWith("Bearer ")
                ? Optional.of(authorization.substring(7))
                : Optional.empty();
    }

}
