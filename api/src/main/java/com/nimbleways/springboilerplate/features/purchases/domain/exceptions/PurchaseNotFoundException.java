package com.nimbleways.springboilerplate.features.purchases.domain.exceptions;

import java.util.UUID;

@lombok.Getter
public class PurchaseNotFoundException extends com.nimbleways.springboilerplate.common.domain.exceptions.AbstractItemNotFoundException {
    public PurchaseNotFoundException(UUID purchaseId) {
        super(String.format("Purchase with id %s not found", purchaseId));
    }
}
