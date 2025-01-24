package ru.moonlightapp.backend.core.storage.model;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public final class TokenPairId implements Serializable {

    @Column(name = "access_token")
    private String accessToken;

    @Column(name = "refresh_token", length = 511)
    private String refreshToken;

}
