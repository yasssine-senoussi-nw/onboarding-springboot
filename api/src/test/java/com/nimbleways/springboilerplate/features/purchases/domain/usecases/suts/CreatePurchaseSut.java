package com.nimbleways.springboilerplate.features.purchases.domain.usecases.suts;

import com.nimbleways.springboilerplate.common.infra.adapters.fakes.FakePurchaseRepository;
import com.nimbleways.springboilerplate.features.purchases.domain.entities.Purchase;
import com.nimbleways.springboilerplate.features.purchases.domain.usecases.createpurchase.CreatePurchaseCommand;
import com.nimbleways.springboilerplate.features.purchases.domain.usecases.createpurchase.CreatePurchaseUseCase;
import com.nimbleways.springboilerplate.testhelpers.helpers.UserSessionHelperSut;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Import;

@Getter
@Import({
        CreatePurchaseUseCase.class,
        FakePurchaseRepository.class,
        UserSessionHelperSut.class
})
@RequiredArgsConstructor
public class CreatePurchaseSut {
    @Getter(AccessLevel.NONE)
    private final CreatePurchaseUseCase useCase;

    private final FakePurchaseRepository purchaseRepository;
    private final UserSessionHelperSut sessionHelper;

    public Purchase handle(CreatePurchaseCommand command) {
        return useCase.handle(command);
    }
}
