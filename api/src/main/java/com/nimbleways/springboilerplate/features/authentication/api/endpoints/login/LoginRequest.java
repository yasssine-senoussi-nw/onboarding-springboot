package com.nimbleways.springboilerplate.features.authentication.api.endpoints.login;

import com.nimbleways.springboilerplate.common.domain.valueobjects.Email;
import com.nimbleways.springboilerplate.features.authentication.domain.usecases.login.LoginCommand;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank
    String email,

    @NotBlank
    String password
) {

    public LoginCommand toCommand() {
        return new LoginCommand(
            new Email(email()),
            password()
        );
    }
}
