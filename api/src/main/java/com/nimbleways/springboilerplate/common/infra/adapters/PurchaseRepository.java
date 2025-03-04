package com.nimbleways.springboilerplate.common.infra.adapters;

import com.nimbleways.springboilerplate.common.domain.valueobjects.UserId;
import com.nimbleways.springboilerplate.common.infra.database.entities.PurchaseDbEntity;
import com.nimbleways.springboilerplate.common.infra.database.jparepositories.JpaPurchaseRepository;
import com.nimbleways.springboilerplate.features.purchases.domain.entities.Purchase;
import com.nimbleways.springboilerplate.features.purchases.domain.ports.PurchaseRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class PurchaseRepository implements PurchaseRepositoryPort {
    private final JpaPurchaseRepository purchaseRepository;

    public PurchaseRepository(JpaPurchaseRepository purchaseRepository) {
        this.purchaseRepository = purchaseRepository;
    }

    @Override
    public Stream<Purchase> findByUserId(UserId userId) {
        return purchaseRepository
                .streamByUser_Id(userId.value())
                .map(PurchaseDbEntity::toPurchase);
    }
}
