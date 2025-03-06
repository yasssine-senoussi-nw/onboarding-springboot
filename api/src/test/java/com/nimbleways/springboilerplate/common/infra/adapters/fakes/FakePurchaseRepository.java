package com.nimbleways.springboilerplate.common.infra.adapters.fakes;

import com.nimbleways.springboilerplate.common.utils.collections.Mutable;
import com.nimbleways.springboilerplate.features.purchases.domain.entities.Purchase;
import com.nimbleways.springboilerplate.features.purchases.domain.ports.PurchaseRepositoryPort;
import com.nimbleways.springboilerplate.features.purchases.domain.valueobjects.NewPurchase;
import org.eclipse.collections.api.map.MutableMap;

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
                purchase.purchaseDate(),
                purchase.brand(),
                purchase.price()
        );
        purchasesTable.put(id, entity);

        return entity;
    }

    @Override
    public Stream<Purchase> findByUserId(UUID userId) {
        return purchasesTable.values()
                .stream()
                .filter(purchase -> purchase.userId().equals(userId));
    }
}
