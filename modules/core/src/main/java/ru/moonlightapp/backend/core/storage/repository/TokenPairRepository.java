package ru.moonlightapp.backend.core.storage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.moonlightapp.backend.core.storage.model.TokenPair;
import ru.moonlightapp.backend.core.storage.model.TokenPairId;

public interface TokenPairRepository extends JpaRepository<TokenPair, TokenPairId> {

    TokenPair getByAccessTokenAndUserEmail(String accessToken, String userEmail);

}
