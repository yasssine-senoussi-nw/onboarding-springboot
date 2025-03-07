package com.nimbleways.springboilerplate.testhelpers.fixtures;

import com.nimbleways.springboilerplate.common.domain.ports.TimeProviderPort;
import com.nimbleways.springboilerplate.common.domain.valueobjects.Money;
import com.nimbleways.springboilerplate.common.domain.valueobjects.StarRating;
import com.nimbleways.springboilerplate.common.utils.collections.Immutable;
import com.nimbleways.springboilerplate.features.purchases.domain.valueobjects.NewPurchase;
import com.nimbleways.springboilerplate.testhelpers.configurations.TimeTestConfiguration;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import org.jetbrains.annotations.NotNull;
import org.eclipse.collections.api.list.ImmutableList;

public class NewPurchaseFixture {

    @NotNull
    public static Builder aNewPurchase() {
        return new Builder();
    }

    @NoArgsConstructor
    @With
    @AllArgsConstructor
    public static final class Builder {
        private String name = "nameX";
        private String brand = "brandX";
        private String model = "modelX";
        private String store = "storeX";
        private Money price = new Money(100.0);
        private StarRating rating = new StarRating(5);
        private ImmutableList<String> images = Immutable.list.of("image1", "image2");
        private TimeProviderPort timeProvider = TimeTestConfiguration.fixedTimeProvider();

        @NotNull
        public NewPurchase build(UUID userId) {
            return new NewPurchase(
                userId,
                name,
                timeProvider.instant(),
                brand,
                model,
                store,
                images,
                price,
                rating
            );
        }
    }
}
