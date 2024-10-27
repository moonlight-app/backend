package ru.moonlightapp.backend.storage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.moonlightapp.backend.storage.model.TokenPair;

public interface TokenPairRepository extends JpaRepository<TokenPair, Long> {

    @Query("SELECT p FROM TokenPair p WHERE p.accessToken = ?1 AND p.refreshToken = ?2")
    TokenPair getByBothTokens(String accessToken, String refreshToken);

    TokenPair getByAccessTokenAndUserEmail(String accessToken, String userEmail);

}
