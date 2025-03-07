package com.nimbleways.springboilerplate.features.purchases.api.endpoints.getcoworkerspurchase;

import com.nimbleways.springboilerplate.features.purchases.domain.usecases.getcoworkerspurchase.GetCoworkersPurchaseCommand;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record GetCoworkersPurchaseRequest(
    @NotBlank
    String userId
) {
    public GetCoworkersPurchaseCommand toCommand() {
        return new GetCoworkersPurchaseCommand(UUID.fromString(userId));
    }
}
