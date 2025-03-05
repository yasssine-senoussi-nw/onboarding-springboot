package com.nimbleways.springboilerplate.features.purchases.domain.usecases.suts;

import com.nimbleways.springboilerplate.common.infra.adapters.fakes.FakePurchaseRepository;
import com.nimbleways.springboilerplate.features.purchases.domain.entities.Purchase;
import com.nimbleways.springboilerplate.features.purchases.domain.usecases.getpurchases.GetPurchasesUseCase;
import com.nimbleways.springboilerplate.testhelpers.helpers.UserSessionHelperSut;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Import;

import java.util.stream.Stream;

@Getter
@Import({
        GetPurchasesUseCase.class,
        FakePurchaseRepository.class,
        UserSessionHelperSut.class
})
@RequiredArgsConstructor
public class GetPurchasesSut {
    @Getter(AccessLevel.NONE)
    private final GetPurchasesUseCase useCase;

    private final FakePurchaseRepository purchaseRepository;
    private final UserSessionHelperSut sessionHelper;

    public Stream<Purchase> handle() {
        return useCase.handle();
    }
}
