package com.nimbleways.springboilerplate.features.purchases.domain.usecases.suts;

import com.nimbleways.springboilerplate.common.infra.adapters.fakes.FakePurchaseRepository;
import com.nimbleways.springboilerplate.features.purchases.domain.entities.Purchase;
import com.nimbleways.springboilerplate.features.purchases.domain.usecases.getpurchase.GetPurchaseCommand;
import com.nimbleways.springboilerplate.features.purchases.domain.usecases.getpurchase.GetPurchaseUseCase;
import com.nimbleways.springboilerplate.testhelpers.helpers.UserSessionHelperSut;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Import;

@Getter
@Import({
        GetPurchaseUseCase.class,
        FakePurchaseRepository.class,
        UserSessionHelperSut.class
})
@RequiredArgsConstructor
public class GetPurchaseSut {
    @Getter(AccessLevel.NONE)
    private final GetPurchaseUseCase useCase;

    private final FakePurchaseRepository purchaseRepository;
    private final UserSessionHelperSut sessionHelper;

    public Purchase handle(GetPurchaseCommand command) {
        return useCase.handle(command);
    }
}
