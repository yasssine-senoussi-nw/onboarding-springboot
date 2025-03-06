package com.nimbleways.springboilerplate.features.purchases.api.endpoints.getpurchase;

import com.nimbleways.springboilerplate.features.purchases.domain.entities.Purchase;
import com.nimbleways.springboilerplate.features.purchases.domain.usecases.getpurchase.GetPurchaseCommand;
import com.nimbleways.springboilerplate.features.purchases.domain.usecases.getpurchase.GetPurchaseUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class GetPurchaseEndpoint {
    private static final String URL = "/purchases/{purchaseId}";

    private final GetPurchaseUseCase getPurchasesUseCase;

    public GetPurchaseEndpoint(GetPurchaseUseCase getPurchasesUseCase) {
        this.getPurchasesUseCase = getPurchasesUseCase;
    }

    @GetMapping(URL)
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.OK)
    public GetPurchaseResponse getPurchase(
            @PathVariable String purchaseId) {
        UUID id = UUID.fromString(purchaseId);
        Purchase purchase = getPurchasesUseCase.handle(new GetPurchaseCommand(id));
        return GetPurchaseResponse.from(purchase);
    }
}
