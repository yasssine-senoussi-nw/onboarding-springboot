package com.nimbleways.springboilerplate.features.purchases.domain.usecases.getcoworkerspurchase;

import com.nimbleways.springboilerplate.features.purchases.domain.entities.Purchase;
import com.nimbleways.springboilerplate.features.purchases.domain.ports.PurchaseRepositoryPort;

import java.util.stream.Stream;

public class GetCoworkersPurchaseUseCase {
	private final PurchaseRepositoryPort purchaseRepository;

	public GetCoworkersPurchaseUseCase(PurchaseRepositoryPort purchaseRepository) {
		this.purchaseRepository = purchaseRepository;
	}

	public Stream<Purchase> handle(GetCoworkersPurchaseCommand command) {
		return purchaseRepository.findCoworkersPurchases(command.userId());
	}
}
