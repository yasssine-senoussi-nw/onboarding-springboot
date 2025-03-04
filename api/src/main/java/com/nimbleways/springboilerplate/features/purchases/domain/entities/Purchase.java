package com.nimbleways.springboilerplate.features.purchases.domain.entities;

import java.time.Instant;
import java.util.UUID;

public record Purchase(UUID id, UUID userId, Instant purchaseDate) {
}
