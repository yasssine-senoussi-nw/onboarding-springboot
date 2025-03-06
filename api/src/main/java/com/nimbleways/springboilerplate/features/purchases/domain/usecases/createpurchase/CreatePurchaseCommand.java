package com.nimbleways.springboilerplate.features.purchases.domain.usecases.createpurchase;

import com.nimbleways.springboilerplate.common.domain.valueobjects.Money;
import com.nimbleways.springboilerplate.common.domain.valueobjects.StarRating;
import com.nimbleways.springboilerplate.features.purchases.domain.valueobjects.NewPurchase;
import org.eclipse.collections.api.list.ImmutableList;

import java.time.Instant;
import java.util.UUID;

public record CreatePurchaseCommand(
    UUID userId,

    String name,

    String brand,
    String model,
    String store,

    ImmutableList<String> images,

    Money price,
    StarRating rating
) {
    public NewPurchase toNewPurchase(Instant purchaseDate) {
        return new NewPurchase(
            userId,
            name,
            purchaseDate,
            brand,
            model,
            store,
            images,
            price,
            rating
        );
    }
}
