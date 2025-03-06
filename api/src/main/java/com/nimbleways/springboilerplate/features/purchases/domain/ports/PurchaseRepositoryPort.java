package com.nimbleways.springboilerplate.features.purchases.domain.ports;

import com.nimbleways.springboilerplate.features.purchases.domain.entities.Purchase;
import com.nimbleways.springboilerplate.features.purchases.domain.valueobjects.NewPurchase;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public interface PurchaseRepositoryPort {
    Purchase create(NewPurchase purchase);

    Optional<Purchase> findById(UUID purchaseId);

    Stream<Purchase> findByUserId(UUID userId);
}
