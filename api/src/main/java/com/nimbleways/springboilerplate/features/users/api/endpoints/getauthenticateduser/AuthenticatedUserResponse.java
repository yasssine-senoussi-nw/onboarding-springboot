package com.nimbleways.springboilerplate.features.users.api.endpoints.getauthenticateduser;

import com.nimbleways.springboilerplate.features.users.domain.entities.User;

public record AuthenticatedUserResponse(String name, String email, double balance) {
    public static AuthenticatedUserResponse from(User user) {
        return new AuthenticatedUserResponse(
                user.name(),
                user.email().value(),
                user.balance().value()
        );
    }
}
