package com.nimbleways.springboilerplate.features.purchases.api.endpoints.createpurchase;

import com.nimbleways.springboilerplate.features.purchases.domain.entities.Purchase;
import com.nimbleways.springboilerplate.features.purchases.domain.usecases.createpurchase.CreatePurchaseCommand;
import com.nimbleways.springboilerplate.features.purchases.domain.usecases.createpurchase.CreatePurchaseUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CreatePurchaseEndpoint {
    private static final String URL = "/purchases/create";

    private final CreatePurchaseUseCase createPurchaseUseCase;

    public CreatePurchaseEndpoint(CreatePurchaseUseCase createPurchaseUseCase) {
        this.createPurchaseUseCase = createPurchaseUseCase;
    }

    @PostMapping(URL)
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.CREATED)
    public CreatePurchaseResponse createPurchase(@RequestBody @Valid final CreatePurchaseRequest request) {
        CreatePurchaseCommand command = request.toCommand();
        Purchase purchase = createPurchaseUseCase.handle(command);
        return CreatePurchaseResponse.from(purchase);
    }
}
