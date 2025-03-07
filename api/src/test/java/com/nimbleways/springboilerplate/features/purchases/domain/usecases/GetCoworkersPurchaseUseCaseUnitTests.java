package com.nimbleways.springboilerplate.features.purchases.domain.usecases;

import com.nimbleways.springboilerplate.features.purchases.domain.entities.Purchase;
import com.nimbleways.springboilerplate.features.purchases.domain.usecases.getcoworkerspurchase.GetCoworkersPurchaseCommand;
import com.nimbleways.springboilerplate.features.purchases.domain.usecases.suts.GetCoworkersPurchaseSut;
import com.nimbleways.springboilerplate.features.users.domain.entities.User;
import com.nimbleways.springboilerplate.testhelpers.annotations.UnitTest;
import com.nimbleways.springboilerplate.testhelpers.utils.Instance;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.nimbleways.springboilerplate.testhelpers.fixtures.NewPurchaseFixture.aNewPurchase;
import static org.junit.jupiter.api.Assertions.assertEquals;

@UnitTest
class GetCoworkersPurchaseUseCaseUnitTests {
    private final GetCoworkersPurchaseSut sut = Instance.create(GetCoworkersPurchaseSut.class);

    @Test
    void returns_list_of_other_purchases_when_authenticated() {
        // GIVEN
        User user1 = sut.sessionHelper().addUserAndSessionToRepository().user();
        User user2 = sut.sessionHelper().addUserAndSessionToRepository().user();

        Purchase purchase1 = sut.purchaseRepository().create(aNewPurchase().build(user1.id()));
        Purchase purchase2 = sut.purchaseRepository().create(aNewPurchase().build(user2.id()));

        // WHEN
        List<Purchase> result1 = sut.handle(new GetCoworkersPurchaseCommand(user1.id())).toList();
        List<Purchase> result2 = sut.handle(new GetCoworkersPurchaseCommand(user2.id())).toList();

        // THEN
        assertEquals(List.of(purchase2), result1);
        assertEquals(List.of(purchase1), result2);
    }
}
