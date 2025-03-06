package com.nimbleways.springboilerplate.features.purchases.domain.valueobjects;

import com.nimbleways.springboilerplate.common.domain.valueobjects.Money;

import java.time.Instant;
import java.util.UUID;

public record NewPurchase(
        UUID userId,
        Instant purchaseDate,
        String brand,
        Money price
) {
}
