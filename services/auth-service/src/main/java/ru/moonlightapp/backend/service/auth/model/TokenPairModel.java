package ru.moonlightapp.backend.service.auth.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TokenPairModel(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("access_token_expires_in") long accessTokenExpiresIn,
        @JsonProperty("refresh_token") String refreshToken,
        @JsonProperty("refresh_token_expires_in") long refreshTokenExpiresIn
) {

}
