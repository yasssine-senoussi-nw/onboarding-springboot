package com.nimbleways.springboilerplate.features.purchases.domain.valueobjects;

import java.time.Instant;
import java.util.UUID;

public record NewPurchase(
        UUID userId,
        Instant purchaseDate
) {
}
