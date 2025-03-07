package com.nimbleways.springboilerplate.features.purchases.domain.usecases;

import com.nimbleways.springboilerplate.features.purchases.domain.entities.Purchase;
import com.nimbleways.springboilerplate.features.purchases.domain.usecases.suts.GetPurchasesSut;
import com.nimbleways.springboilerplate.features.users.domain.entities.User;
import com.nimbleways.springboilerplate.testhelpers.annotations.UnitTest;
import com.nimbleways.springboilerplate.testhelpers.utils.Instance;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.nimbleways.springboilerplate.testhelpers.fixtures.NewPurchaseFixture.aNewPurchase;
import static org.junit.jupiter.api.Assertions.assertEquals;

@UnitTest
class GetPurchasesUseCaseUnitTests {
    private final GetPurchasesSut sut = Instance.create(GetPurchasesSut.class);

    @Test
    void returns_list_of_purchases_when_authenticated() {
        // GIVEN
        User user = sut.sessionHelper().addUserAndSessionToRepository().user();
        Purchase purchase = sut.purchaseRepository().create(aNewPurchase().build(user.id()));

        // WHEN
        List<Purchase> result = sut.handle().toList();

        // THEN
        assertEquals(List.of(purchase), result);
    }
}
