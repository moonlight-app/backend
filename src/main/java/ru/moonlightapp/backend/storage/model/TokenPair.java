package ru.moonlightapp.backend.storage.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.function.BiFunction;

@Getter
@Builder(setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity @IdClass(TokenPairId.class) @Table(name = "token_pairs")
public final class TokenPair {

    @Id
    @Column(name = "access_token", nullable = false)
    private String accessToken;

    @Id
    @Column(name = "refresh_token", nullable = false, length = 511)
    private String refreshToken;

    @Column(name = "access_token_expires_at", nullable = false)
    private Instant accessTokenExpiresAt;

    @Column(name = "refresh_token_expires_at", nullable = false)
    private Instant refreshTokenExpiresAt;

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public TokenPair(String userEmail, String accessToken, Instant accessTokenExpiresIn, String refreshToken, Instant refreshTokenExpiresIn) {
        this.userEmail = userEmail;
        this.accessToken = accessToken;
        this.accessTokenExpiresAt = accessTokenExpiresIn;
        this.refreshToken = refreshToken;
        this.refreshTokenExpiresAt = refreshTokenExpiresIn;
        this.createdAt = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        this.updatedAt = createdAt;
    }

    public boolean isAccessTokenExpired() {
        return accessTokenExpiresAt.isBefore(Instant.now());
    }

    public boolean isRefreshTokenExpired() {
        return refreshTokenExpiresAt.isBefore(Instant.now());
    }

    public void updateAccessToken(BiFunction<String, Instant, String> tokenGenerator, Instant expiresAt) {
        updateAccessToken(tokenGenerator.apply(userEmail, expiresAt), expiresAt);
    }

    public void updateAccessToken(String token, Instant expiresAt) {
        this.accessToken = token;
        this.accessTokenExpiresAt = expiresAt;
        onDataUpdated();
    }

    public void updateRefreshToken(BiFunction<String, Instant, String> tokenGenerator, Instant expiresAt) {
        updateRefreshToken(tokenGenerator.apply(userEmail, expiresAt), expiresAt);
    }

    public void updateRefreshToken(String token, Instant expiresAt) {
        this.refreshToken = token;
        this.refreshTokenExpiresAt = expiresAt;
        onDataUpdated();
    }

    private void onDataUpdated() {
        this.updatedAt = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TokenPair tokenPair = (TokenPair) o;
        return Objects.equals(accessToken, tokenPair.accessToken) &&
                Objects.equals(refreshToken, tokenPair.refreshToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accessToken, refreshToken);
    }

    @Override
    public String toString() {
        return "TokenPair{" +
                "accessToken='" + (accessToken != null ? "<masked>" : "<not set>") + '\'' +
                ", accessTokenExpiresAt=" + accessTokenExpiresAt +
                ", refreshToken='" + (refreshToken != null ? "<masked>" : "<not set>") + '\'' +
                ", refreshTokenExpiresAt=" + refreshTokenExpiresAt +
                ", userEmail='" + userEmail + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

}
