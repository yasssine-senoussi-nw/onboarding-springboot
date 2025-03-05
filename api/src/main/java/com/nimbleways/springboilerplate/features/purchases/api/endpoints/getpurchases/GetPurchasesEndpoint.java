package com.nimbleways.springboilerplate.features.purchases.api.endpoints.getpurchases;

import com.nimbleways.springboilerplate.features.purchases.domain.entities.Purchase;
import com.nimbleways.springboilerplate.features.purchases.domain.usecases.getpurchases.GetPurchasesUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Stream;

@RestController
public class GetPurchasesEndpoint {
    private static final String URL = "/purchases/get";

    private final GetPurchasesUseCase getPurchasesUseCase;

    public GetPurchasesEndpoint(GetPurchasesUseCase getPurchasesUseCase) {
        this.getPurchasesUseCase = getPurchasesUseCase;
    }

    @GetMapping(URL)
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.OK)
    @Transactional(readOnly = true)
    public GetPurchasesResponse getAuthenticatedUser() {
        try (Stream<Purchase> purchases = getPurchasesUseCase.handle()) {
             return GetPurchasesResponse.from(purchases);
        }
    }
}
