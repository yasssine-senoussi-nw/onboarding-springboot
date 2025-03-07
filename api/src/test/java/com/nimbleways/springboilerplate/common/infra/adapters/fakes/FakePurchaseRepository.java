package com.nimbleways.springboilerplate.common.infra.adapters.fakes;

import com.nimbleways.springboilerplate.common.utils.collections.Mutable;
import com.nimbleways.springboilerplate.features.purchases.domain.entities.Purchase;
import com.nimbleways.springboilerplate.features.purchases.domain.ports.PurchaseRepositoryPort;
import com.nimbleways.springboilerplate.features.purchases.domain.valueobjects.NewPurchase;
import org.eclipse.collections.api.map.MutableMap;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public class FakePurchaseRepository implements PurchaseRepositoryPort {

    private final MutableMap<UUID, Purchase> purchasesTable = Mutable.map.empty();

    @Override
    public Purchase create(NewPurchase purchase) {
        UUID id = UUID.randomUUID();

        Purchase entity = new Purchase(
            id,
            purchase.userId(),
            purchase.name(),
            purchase.purchaseDate(),
            purchase.brand(),
            purchase.model(),
            purchase.store(),
            purchase.images(),
            purchase.price(),
            purchase.rating()
        );
        purchasesTable.put(id, entity);

        return entity;
    }

    @Override
    public Optional<Purchase> findById(UUID purchaseId) {
        return Optional.ofNullable(purchasesTable.get(purchaseId));
    }

    @Override
    public Stream<Purchase> findByUserId(UUID userId) {
        return purchasesTable.values()
            .stream()
            .filter(purchase -> purchase.userId().equals(userId));
    }

    @Override
    public Stream<Purchase> findCoworkersPurchases(UUID userId) {
        return purchasesTable.values()
            .stream()
            .filter(purchase -> !purchase.userId().equals(userId));
    }
}
