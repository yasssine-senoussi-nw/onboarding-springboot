package com.nimbleways.springboilerplate.features.purchases.domain.exceptions;

import com.nimbleways.springboilerplate.common.domain.exceptions.AbstractItemNotFoundException;
import lombok.Getter;

import java.util.UUID;

@Getter
public class PurchaseNotFoundException extends AbstractItemNotFoundException {
    public PurchaseNotFoundException(UUID purchaseId) {
        super(String.format("Purchase with id %s not found", purchaseId));
    }
}
