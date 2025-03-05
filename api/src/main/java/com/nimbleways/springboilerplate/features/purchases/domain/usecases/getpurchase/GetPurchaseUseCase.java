package com.nimbleways.springboilerplate.features.purchases.domain.usecases.getpurchase;

import com.nimbleways.springboilerplate.features.purchases.domain.entities.Purchase;
import com.nimbleways.springboilerplate.features.purchases.domain.exceptions.PurchaseNotFoundException;
import com.nimbleways.springboilerplate.features.purchases.domain.ports.PurchaseRepositoryPort;

public class GetPurchaseUseCase {
    private final PurchaseRepositoryPort purchaseRepository;

    public GetPurchaseUseCase(PurchaseRepositoryPort purchaseRepository) {
        this.purchaseRepository = purchaseRepository;
    }

    public Purchase handle(GetPurchaseCommand command) {
        return purchaseRepository
                .findById(command.purchaseId())
                .orElseThrow(() -> new PurchaseNotFoundException(command.purchaseId()));
    }
}
