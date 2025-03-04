package com.nimbleways.springboilerplate.features.purchases.api.endpoints.getpurchases;

import com.nimbleways.springboilerplate.features.purchases.domain.entities.Purchase;

import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class GetPurchasesResponse extends ArrayList<GetPurchasesResponse.Item> {
    public record Item(
            UUID id,
            Instant purchaseDate
    ) {
        public static Item from(Purchase user) {
            return new Item(
                    user.id(),
                    user.purchaseDate()
            );
        }
    }

    public static GetPurchasesResponse from(Stream<Purchase> purchases) {
        return purchases
                .map(Item::from)
                .collect(Collectors.toCollection(GetPurchasesResponse::new));
    }
}
