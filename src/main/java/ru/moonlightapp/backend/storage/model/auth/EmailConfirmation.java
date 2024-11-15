package ru.moonlightapp.backend.storage.model.auth;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

import static ru.moonlightapp.backend.web.auth.service.EmailConfirmationService.*;

@Getter
@Builder(setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity @Table(name = "email_confirmations")
public final class EmailConfirmation {

    @Id
    @Column(name = "email", nullable = false, length = 64)
    private String email;

    @Column(name = "code", nullable = false, length = 6)
    private String code;

    @Column(name = "proof_key", nullable = false, length = 32)
    private String proofKey;

    @Column(name = "attempts_left", nullable = false)
    private int attemptsLeft;

    @Column(name = "is_confirmed", nullable = false)
    private boolean confirmed;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "requested_at", nullable = false)
    private Instant requestedAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public EmailConfirmation(String email) {
        this.email = email;
        this.createdAt = Instant.now();
        this.updatedAt = createdAt;
    }

    public boolean hasNoMoreAttempts() {
        return attemptsLeft <= 0;
    }

    public boolean isUnrenewable() {
        return isExpired() || requestedAt.plusSeconds(REQUEST_CAN_BE_RENEWED_IN).isBefore(Instant.now());
    }

    public boolean isExpired() {
        return requestedAt == null || requestedAt.plusSeconds(REQUEST_WILL_BE_EXPIRED_IN).isBefore(Instant.now());
    }

    public void decreaseAttemptsCounter() {
        this.attemptsLeft--;
        onDataUpdated();
    }

    public void renew(String code, String proofKey) {
        this.code = code;
        this.proofKey = proofKey;
        this.attemptsLeft = DEFAULT_CONFIRM_ATTEMPTS;
        this.requestedAt = Instant.now();
        onDataUpdated();
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
        onDataUpdated();
    }

    private void onDataUpdated() {
        this.updatedAt = Instant.now();
    }

}