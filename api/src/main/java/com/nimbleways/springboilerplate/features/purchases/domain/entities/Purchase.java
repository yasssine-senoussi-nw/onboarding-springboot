package com.nimbleways.springboilerplate.features.purchases.domain.entities;

import com.nimbleways.springboilerplate.common.domain.valueobjects.Money;
import com.nimbleways.springboilerplate.common.domain.valueobjects.StarRating;
import org.eclipse.collections.api.list.ImmutableList;

import java.time.Instant;
import java.util.UUID;

public record Purchase(
	UUID id,
	UUID userId,

	String name,
	Instant purchaseDate,

	String brand,
	String model,
	String store,

	ImmutableList<String> images,

	Money price,
	StarRating rating
) {
}
