package com.nimbleways.springboilerplate.features.purchases.domain.usecases.suts;

import com.nimbleways.springboilerplate.common.infra.adapters.fakes.FakePurchaseRepository;
import com.nimbleways.springboilerplate.features.purchases.domain.entities.Purchase;
import com.nimbleways.springboilerplate.features.purchases.domain.usecases.getcoworkerspurchase.GetCoworkersPurchaseCommand;
import com.nimbleways.springboilerplate.features.purchases.domain.usecases.getcoworkerspurchase.GetCoworkersPurchaseUseCase;
import com.nimbleways.springboilerplate.testhelpers.helpers.UserSessionHelperSut;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Import;

import java.util.stream.Stream;

@Getter
@Import({
    GetCoworkersPurchaseUseCase.class,
    FakePurchaseRepository.class,
    UserSessionHelperSut.class
})
@RequiredArgsConstructor
public class GetCoworkersPurchaseSut {
    @Getter(AccessLevel.NONE)
    private final GetCoworkersPurchaseUseCase useCase;

    private final FakePurchaseRepository purchaseRepository;
    private final UserSessionHelperSut sessionHelper;

    public Stream<Purchase> handle(GetCoworkersPurchaseCommand command) {
        return useCase.handle(command);
    }
}
