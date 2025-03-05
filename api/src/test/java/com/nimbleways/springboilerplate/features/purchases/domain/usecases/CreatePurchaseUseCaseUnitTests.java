package com.nimbleways.springboilerplate.features.purchases.domain.usecases;

import com.nimbleways.springboilerplate.common.domain.valueobjects.Money;
import com.nimbleways.springboilerplate.features.purchases.domain.entities.Purchase;
import com.nimbleways.springboilerplate.features.purchases.domain.usecases.createpurchase.CreatePurchaseCommand;
import com.nimbleways.springboilerplate.features.purchases.domain.usecases.suts.CreatePurchaseSut;
import com.nimbleways.springboilerplate.features.users.domain.entities.User;
import com.nimbleways.springboilerplate.testhelpers.annotations.UnitTest;
import com.nimbleways.springboilerplate.testhelpers.utils.Instance;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@UnitTest
class CreatePurchaseUseCaseUnitTests {
    private final CreatePurchaseSut sut = Instance.create(CreatePurchaseSut.class);

    @Test
    void create_valid_purchase_with_same_values_as_command() {
        // GIVEN
        User user = sut.sessionHelper().addUserAndSessionToRepository().user();
        CreatePurchaseCommand command = new CreatePurchaseCommand(
                user.id(),
                "brand",
                new Money(19)
        );

        // WHEN
        Purchase result = sut.handle(command);

        // THEN
        assertEquals(command.userId(), result.userId());
        assertEquals(command.brand(), result.brand());
        assertEquals(command.price(), result.price());
    }
}
