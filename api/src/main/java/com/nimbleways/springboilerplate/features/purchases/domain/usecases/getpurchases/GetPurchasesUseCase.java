package com.nimbleways.springboilerplate.features.purchases.domain.usecases.getpurchases;

import com.nimbleways.springboilerplate.common.domain.ports.SecurityContextPort;
import com.nimbleways.springboilerplate.features.purchases.domain.entities.Purchase;
import com.nimbleways.springboilerplate.features.purchases.domain.ports.PurchaseRepositoryPort;

import java.util.UUID;
import java.util.stream.Stream;

public class GetPurchasesUseCase {
    private final PurchaseRepositoryPort purchaseRepository;
    private final SecurityContextPort securityContextPort;

    public GetPurchasesUseCase(PurchaseRepositoryPort purchaseRepository, SecurityContextPort securityContextPort) {
        this.purchaseRepository = purchaseRepository;
        this.securityContextPort = securityContextPort;
    }

    public Stream<Purchase> handle() {
        final UUID userId = securityContextPort.getCurrentUserId().orElseThrow();
        return purchaseRepository.findByUserId(userId);
    }
}
