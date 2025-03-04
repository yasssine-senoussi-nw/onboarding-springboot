package com.nimbleways.springboilerplate.features.purchases.domain.ports;

import com.nimbleways.springboilerplate.common.domain.valueobjects.UserId;
import com.nimbleways.springboilerplate.features.purchases.domain.entities.Purchase;

import java.util.stream.Stream;

public interface PurchaseRepositoryPort {
    Stream<Purchase> findByUserId(UserId userId);
}
