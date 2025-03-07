package com.nimbleways.springboilerplate.features.purchases.api.endpoints.createpurchase;

import com.nimbleways.springboilerplate.common.domain.valueobjects.Money;
import com.nimbleways.springboilerplate.common.domain.valueobjects.StarRating;
import com.nimbleways.springboilerplate.features.purchases.domain.usecases.createpurchase.CreatePurchaseCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.eclipse.collections.api.list.ImmutableList;
import org.hibernate.validator.constraints.Range;

import java.util.UUID;

public record CreatePurchaseRequest(
    @NotBlank
    String userId,

    @NotBlank
    String name,

    @NotBlank
    String brand,

    @NotBlank
    String model,

    @NotBlank
    String store,

    @NotNull
    ImmutableList<String> images,

    @Range(min = 0)
    @NotNull
    Double price,

    @Range(min = 0, max = 5)
    @NotNull
    Integer rating
) {
    public CreatePurchaseCommand toCommand() {
        return new CreatePurchaseCommand(
            UUID.fromString(userId),
            name,
            brand,
            model,
            store,
            images,
            new Money(price),
            new StarRating(rating)
        );
    }
}
