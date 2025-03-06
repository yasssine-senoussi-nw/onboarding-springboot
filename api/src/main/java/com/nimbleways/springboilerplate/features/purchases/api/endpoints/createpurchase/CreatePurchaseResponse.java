package com.nimbleways.springboilerplate.features.purchases.api.endpoints.createpurchase;

import com.nimbleways.springboilerplate.features.purchases.domain.entities.Purchase;
import org.hibernate.validator.constraints.Range;

public record CreatePurchaseResponse(
        String id,
        String userId,
        String purchaseDate,
        String brand,
        @Range(min = 0)
        double price
) {
    public static CreatePurchaseResponse from(Purchase purchase) {
        return new CreatePurchaseResponse(
                purchase.id().toString(),
                purchase.userId().toString(),
                purchase.purchaseDate().toString(),
                purchase.brand(),
                purchase.price().value()
        );
    }
}
