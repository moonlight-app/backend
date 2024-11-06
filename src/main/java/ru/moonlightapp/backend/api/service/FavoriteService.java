package ru.moonlightapp.backend.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.moonlightapp.backend.api.model.GridItemModel;
import ru.moonlightapp.backend.exception.ApiException;
import ru.moonlightapp.backend.storage.model.User;
import ru.moonlightapp.backend.storage.model.content.FavoriteProduct;
import ru.moonlightapp.backend.storage.model.content.Product;
import ru.moonlightapp.backend.storage.repository.content.FavoriteProductRepository;
import ru.moonlightapp.backend.storage.repository.content.ProductRepository;
import ru.moonlightapp.backend.storage.specification.ProductSpecs;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public final class FavoriteService {

    private static final int ITEMS_PER_PAGE = 20;

    private final FavoriteProductRepository favoriteProductRepository;
    private final ProductRepository productRepository;

    public void addFavoriteProduct(User owner, long productId) throws ApiException {
        Optional<Product> product = productRepository.findById(productId);
        if (product.isEmpty())
            throw new ApiException("product_not_found", "A product with this ID isn't exist!");

        if (favoriteProductRepository.existsByOwnerEmailAndProductId(owner.getEmail(), productId))
            throw new ApiException("already_exists", "This product is already favorited by this user!");

        favoriteProductRepository.save(new FavoriteProduct(owner, product.get()));
    }

    public Page<GridItemModel> findFavoriteProducts(String ownerEmail, int pageNumber) {
        Specification<Product> specs = ProductSpecs.isFavorite(ownerEmail);
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, ITEMS_PER_PAGE);
        Page<Product> page = productRepository.findAll(specs, pageRequest);
        return page.map(GridItemModel::from);
    }

    public Set<Long> keepOnlyFavoriteIds(String ownerEmail, Collection<Long> ids) throws ApiException {
        return favoriteProductRepository.keepOnlyFavoriteProductIds(ownerEmail, ids);
    }

    public void removeFavoriteProduct(User owner, long productId) throws ApiException {
        if (!productRepository.existsById(productId))
            throw new ApiException("product_not_found", "A product with this ID isn't exist!");

        Optional<FavoriteProduct> existing = favoriteProductRepository.findByOwnerEmailAndProductId(owner.getEmail(), productId);
        if (existing.isEmpty())
            throw new ApiException("favorite_product_not_found", "This product wasn't favorited by this user!");

        favoriteProductRepository.delete(existing.get());
    }

}
