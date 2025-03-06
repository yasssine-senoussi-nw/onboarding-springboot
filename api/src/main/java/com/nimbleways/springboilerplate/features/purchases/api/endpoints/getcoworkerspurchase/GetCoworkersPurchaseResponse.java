package com.nimbleways.springboilerplate.features.purchases.api.endpoints.getcoworkerspurchase;

import com.nimbleways.springboilerplate.features.purchases.domain.entities.Purchase;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*

public record Purchase(
        UUID id,
        UUID userId,

        String name,
        Instant purchaseDate,

        String brand,
        String model,
        String store,

        ImmutableList<String> images,

        Money price
) {
}

 */
public final class GetCoworkersPurchaseResponse extends ArrayList<GetCoworkersPurchaseResponse.Item> {
    public record Item(
        String id,
        String userId,
        String name,
        String purchaseDate,

        String brand,
        String model,
        String store,

        ArrayList<String> images,

        double price,
        int rating
    ) {
        public static Item from(Purchase purchase) {
            return new Item(
                purchase.id().toString(),
                purchase.userId().toString(),
                purchase.name(),
                purchase.purchaseDate().toString(),

                purchase.brand(),
                purchase.model(),
                purchase.store(),

                new ArrayList<>(purchase.images().toList()),

                purchase.price().value(),
                purchase.rating().value()
            );
        }
    }

    public static GetCoworkersPurchaseResponse from(Stream<Purchase> purchases) {
        return purchases
            .map(Item::from)
            .collect(Collectors.toCollection(GetCoworkersPurchaseResponse::new));
    }
}