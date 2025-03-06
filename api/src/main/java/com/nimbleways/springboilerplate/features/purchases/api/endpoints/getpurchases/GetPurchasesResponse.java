package com.nimbleways.springboilerplate.features.purchases.api.endpoints.getpurchases;

import com.nimbleways.springboilerplate.features.purchases.domain.entities.Purchase;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class GetPurchasesResponse extends ArrayList<GetPurchasesResponse.Item> {
	public record Item(
		String id,
		String userId,
		String name,
		String purchaseDate,

		String brand,
		String model,
		String store,

		ArrayList<String> images,

		double price
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

				purchase.price().value()
			);
		}
	}

	public static GetPurchasesResponse from(Stream<Purchase> purchases) {
		return purchases
			.map(Item::from)
			.collect(Collectors.toCollection(GetPurchasesResponse::new));
	}
}
