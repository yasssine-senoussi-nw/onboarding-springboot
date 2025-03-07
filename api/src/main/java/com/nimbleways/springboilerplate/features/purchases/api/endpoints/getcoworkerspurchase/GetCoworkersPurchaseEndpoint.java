package com.nimbleways.springboilerplate.features.purchases.api.endpoints.getcoworkerspurchase;

import com.nimbleways.springboilerplate.features.purchases.domain.entities.Purchase;
import com.nimbleways.springboilerplate.features.purchases.domain.usecases.getcoworkerspurchase.GetCoworkersPurchaseCommand;
import com.nimbleways.springboilerplate.features.purchases.domain.usecases.getcoworkerspurchase.GetCoworkersPurchaseUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Stream;

@RestController
public class GetCoworkersPurchaseEndpoint {
    private final static String URL = "/purchases/getcoworkers";

    private final GetCoworkersPurchaseUseCase getCoworkersPurchaseUseCase;

    public GetCoworkersPurchaseEndpoint(GetCoworkersPurchaseUseCase getCoworkersPurchaseUseCase) {
        this.getCoworkersPurchaseUseCase = getCoworkersPurchaseUseCase;
    }


    @GetMapping(URL)
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.OK)
    @Transactional(readOnly = true)
    public GetCoworkersPurchaseResponse getCoworkersPurchase(
        @RequestBody @Valid GetCoworkersPurchaseRequest request) {
        GetCoworkersPurchaseCommand command = request.toCommand();
        try (Stream<Purchase> purchases = getCoworkersPurchaseUseCase.handle(command)) {
            return GetCoworkersPurchaseResponse.from(purchases);
        }
    }
}
