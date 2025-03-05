package com.nimbleways.springboilerplate.features.purchases.api.endpoints.getpurchases;

import com.nimbleways.springboilerplate.features.purchases.domain.entities.Purchase;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class GetPurchasesResponse extends ArrayList<GetPurchasesResponse.Item> {
    public record Item(
            String id,
            String purchaseDate
    ) {
        public static Item from(Purchase purchase) {
            return new Item(
                    purchase.id().toString(),
                    purchase.purchaseDate().toString()
            );
        }
    }

    public static GetPurchasesResponse from(Stream<Purchase> purchases) {
        return purchases
                .map(Item::from)
                .collect(Collectors.toCollection(GetPurchasesResponse::new));
    }
}
