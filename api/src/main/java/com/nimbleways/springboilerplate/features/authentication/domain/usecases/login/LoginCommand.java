package com.nimbleways.springboilerplate.features.authentication.domain.usecases.login;

import com.nimbleways.springboilerplate.common.domain.valueobjects.Email;

public record LoginCommand(
    Email email,
    String password
) {
}
