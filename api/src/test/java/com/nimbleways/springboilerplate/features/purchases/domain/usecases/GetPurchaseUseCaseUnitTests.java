package com.nimbleways.springboilerplate.features.purchases.domain.usecases;

import com.nimbleways.springboilerplate.features.purchases.domain.entities.Purchase;
import com.nimbleways.springboilerplate.features.purchases.domain.exceptions.PurchaseNotFoundException;
import com.nimbleways.springboilerplate.features.purchases.domain.usecases.getpurchase.GetPurchaseCommand;
import com.nimbleways.springboilerplate.features.purchases.domain.usecases.suts.GetPurchaseSut;
import com.nimbleways.springboilerplate.features.users.domain.entities.User;
import com.nimbleways.springboilerplate.testhelpers.annotations.UnitTest;
import com.nimbleways.springboilerplate.testhelpers.utils.Instance;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static com.nimbleways.springboilerplate.testhelpers.fixtures.NewPurchaseFixture.aNewPurchase;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

@UnitTest
class GetPurchaseUseCaseUnitTests {
    private final GetPurchaseSut sut = Instance.create(GetPurchaseSut.class);

    @Test
    void returns_dummy_purchase() {
        // GIVEN
        User user = sut.sessionHelper().addUserAndSessionToRepository().user();
        Purchase purchase = sut.purchaseRepository().create(aNewPurchase().build(user.id()));
        UUID purchaseId = purchase.id();

        // WHEN
        Purchase result = sut.handle(new GetPurchaseCommand(purchaseId));

        // THEN
        assertEquals(purchase, result);
    }

    @Test
    void throws_when_purchase_id_not_found() {
        // GIVEN
        sut.sessionHelper().addUserAndSessionToRepository();
        UUID purchaseId = UUID.randomUUID();

        // THEN
        assertThrows(
                PurchaseNotFoundException.class,
                () -> sut.handle(new GetPurchaseCommand(purchaseId))
        );
    }
}
