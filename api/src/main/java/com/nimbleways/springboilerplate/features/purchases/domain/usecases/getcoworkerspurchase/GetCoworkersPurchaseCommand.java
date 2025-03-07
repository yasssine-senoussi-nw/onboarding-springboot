package com.nimbleways.springboilerplate.features.purchases.domain.usecases.getcoworkerspurchase;

import java.util.UUID;

public record GetCoworkersPurchaseCommand(
    UUID userId
) {
}
