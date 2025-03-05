package com.nimbleways.springboilerplate.features.purchases.domain.usecases.createpurchase;

import com.nimbleways.springboilerplate.common.domain.valueobjects.Money;
import com.nimbleways.springboilerplate.features.purchases.domain.valueobjects.NewPurchase;

import java.time.Instant;
import java.util.UUID;

public record CreatePurchaseCommand(
        UUID userId,
        String brand,
        Money price
) {
    public NewPurchase toNewPurchase(Instant purchaseDate) {
        return new NewPurchase(
                userId,
                purchaseDate,
                brand,
                price
        );
    }
}
