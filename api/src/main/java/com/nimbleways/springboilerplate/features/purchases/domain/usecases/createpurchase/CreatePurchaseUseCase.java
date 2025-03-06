package com.nimbleways.springboilerplate.features.purchases.domain.usecases.createpurchase;

import com.nimbleways.springboilerplate.common.domain.ports.TimeProviderPort;
import com.nimbleways.springboilerplate.features.purchases.domain.entities.Purchase;
import com.nimbleways.springboilerplate.features.purchases.domain.ports.PurchaseRepositoryPort;

import java.time.Instant;

public class CreatePurchaseUseCase {

    private final PurchaseRepositoryPort purchaseRepository;
    private final TimeProviderPort timeProvider;

    public CreatePurchaseUseCase(PurchaseRepositoryPort purchaseRepository, TimeProviderPort timeProvider) {
        this.purchaseRepository = purchaseRepository;
        this.timeProvider = timeProvider;
    }

    public Purchase handle(CreatePurchaseCommand command) {
        Instant purchaseDate = timeProvider.instant();
        return purchaseRepository.create(command.toNewPurchase(purchaseDate));
    }
}
