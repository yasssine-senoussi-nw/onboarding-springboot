package com.nimbleways.springboilerplate.features.purchases.domain.usecases.getpurchase;

import java.util.UUID;

public record GetPurchaseCommand(
        UUID purchaseId
) {
}
