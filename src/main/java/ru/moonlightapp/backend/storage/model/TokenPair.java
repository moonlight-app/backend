package ru.moonlightapp.backend.storage.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Objects;
import java.util.function.BiFunction;

@Getter
@NoArgsConstructor
@Entity @Table(name = "token_pairs")
public final class TokenPair {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Column(name = "access_token", nullable = false, unique = true, length = 0)
    private String accessToken;

    @Column(name = "access_token_expires_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Instant accessTokenExpiresAt;

    @Column(name = "refresh_token", nullable = false, unique = true, length = 0)
    private String refreshToken;

    @Column(name = "refresh_token_expires_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Instant refreshTokenExpiresAt;

    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Instant updatedAt;

    public TokenPair(String userEmail, String accessToken, Instant accessTokenExpiresIn, String refreshToken, Instant refreshTokenExpiresIn) {
        this.userEmail = userEmail;
        this.accessToken = accessToken;
        this.accessTokenExpiresAt = accessTokenExpiresIn;
        this.refreshToken = refreshToken;
        this.refreshTokenExpiresAt = refreshTokenExpiresIn;
        this.createdAt = Instant.now();
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
        this.updatedAt = Instant.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TokenPair tokenPair = (TokenPair) o;
        return id == tokenPair.id && Objects.equals(userEmail, tokenPair.userEmail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userEmail);
    }

    @Override
    public String toString() {
        return "TokenPair{" +
                "id=" + id +
                ", userEmail='" + userEmail + '\'' +
                ", accessToken='" + (accessToken != null ? "<masked>" : "<not set>") + '\'' +
                ", accessTokenExpiresAt=" + accessTokenExpiresAt +
                ", refreshToken='" + (refreshToken != null ? "<masked>" : "<not set>") + '\'' +
                ", refreshTokenExpiresAt=" + refreshTokenExpiresAt +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

}
