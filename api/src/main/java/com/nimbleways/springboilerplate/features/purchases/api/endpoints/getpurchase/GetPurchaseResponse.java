package com.nimbleways.springboilerplate.features.purchases.api.endpoints.getpurchase;

import com.nimbleways.springboilerplate.features.purchases.domain.entities.Purchase;

import java.util.ArrayList;

public record GetPurchaseResponse(
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
	public static GetPurchaseResponse from(Purchase purchase) {
		return new GetPurchaseResponse(
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
