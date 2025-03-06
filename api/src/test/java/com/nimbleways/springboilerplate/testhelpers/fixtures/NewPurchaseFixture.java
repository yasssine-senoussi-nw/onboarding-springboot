package com.nimbleways.springboilerplate.testhelpers.fixtures;

import com.nimbleways.springboilerplate.common.domain.ports.TimeProviderPort;
import com.nimbleways.springboilerplate.common.domain.valueobjects.Money;
import com.nimbleways.springboilerplate.features.purchases.domain.valueobjects.NewPurchase;
import com.nimbleways.springboilerplate.testhelpers.configurations.TimeTestConfiguration;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import org.jetbrains.annotations.NotNull;

public class NewPurchaseFixture {

    @NotNull
    public static Builder aNewPurchase() {
        return new Builder();
    }

    @NoArgsConstructor
    @With
    @AllArgsConstructor
    public static final class Builder {
        private String brand = "brandX";
        private Money price = new Money(100.0);
        private TimeProviderPort timeProvider = TimeTestConfiguration.fixedTimeProvider();

        @NotNull
        public NewPurchase build(UUID userId) {
            return new NewPurchase(
                    userId,
                    timeProvider.instant(),
                    brand,
                    price);
        }
    }
}
