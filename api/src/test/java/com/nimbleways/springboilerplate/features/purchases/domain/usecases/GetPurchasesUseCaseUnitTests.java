package com.nimbleways.springboilerplate.features.purchases.domain.usecases;

import com.nimbleways.springboilerplate.features.purchases.domain.entities.Purchase;
import com.nimbleways.springboilerplate.features.purchases.domain.usecases.suts.GetPurchasesSut;
import com.nimbleways.springboilerplate.features.purchases.domain.valueobjects.NewPurchase;
import com.nimbleways.springboilerplate.features.users.domain.entities.User;
import com.nimbleways.springboilerplate.testhelpers.annotations.UnitTest;
import com.nimbleways.springboilerplate.testhelpers.utils.Instance;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@UnitTest
class GetPurchasesUseCaseUnitTests {
    private final GetPurchasesSut sut = Instance.create(GetPurchasesSut.class);

    @Test
    void returns_authenticated_user() {
        // GIVEN
        User user = sut.sessionHelper().addUserAndSessionToRepository().user();
        Purchase purchase = sut.purchaseRepository().create(new NewPurchase(user.id(), Instant.now()));

        // WHEN
        List<Purchase> result = sut.handle().toList();

        // THEN
        assertEquals(List.of(purchase), result);
    }
}
