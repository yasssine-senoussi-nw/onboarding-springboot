package com.nimbleways.springboilerplate.features.purchases.api.endpoints.createpurchase;

import com.nimbleways.springboilerplate.common.domain.valueobjects.Money;
import com.nimbleways.springboilerplate.features.purchases.domain.usecases.createpurchase.CreatePurchaseCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Range;

import java.util.UUID;

public record CreatePurchaseRequest(
        @NotBlank
        String userId,
        @NotBlank
        String brand,
        @Range(min = 0)
        @NotNull
        Double price
) {
    public CreatePurchaseCommand toCommand() {
        return new CreatePurchaseCommand(
                UUID.fromString(userId),
                brand,
                new Money(price)
        );
    }
}
