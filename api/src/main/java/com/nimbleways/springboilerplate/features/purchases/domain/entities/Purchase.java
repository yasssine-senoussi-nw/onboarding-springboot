package com.nimbleways.springboilerplate.features.purchases.domain.entities;

import com.nimbleways.springboilerplate.common.domain.valueobjects.Money;

import java.time.Instant;
import java.util.UUID;

public record Purchase(
        UUID id,
        UUID userId,
        Instant purchaseDate,
        String brand,
        Money price
) {
}
