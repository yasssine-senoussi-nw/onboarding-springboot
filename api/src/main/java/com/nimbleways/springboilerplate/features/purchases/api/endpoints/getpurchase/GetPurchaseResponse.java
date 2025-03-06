package com.nimbleways.springboilerplate.features.purchases.api.endpoints.getpurchase;

import com.nimbleways.springboilerplate.features.purchases.domain.entities.Purchase;

public record GetPurchaseResponse(
        String id,
        String userId,
        String purchaseDate
) {
    public static GetPurchaseResponse from(Purchase purchase) {
        return new GetPurchaseResponse(
                purchase.id().toString(),
                purchase.userId().toString(),
                purchase.purchaseDate().toString()
        );
    }
}
