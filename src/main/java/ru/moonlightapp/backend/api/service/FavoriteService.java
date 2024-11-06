package ru.moonlightapp.backend.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.moonlightapp.backend.storage.repository.content.FavoriteProductRepository;

@Service
@RequiredArgsConstructor
public final class FavoriteService {

    private final FavoriteProductRepository favoriteProductRepository;

}
