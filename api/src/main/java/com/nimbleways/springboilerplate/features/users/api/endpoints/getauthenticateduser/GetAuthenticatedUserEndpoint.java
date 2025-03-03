package com.nimbleways.springboilerplate.features.users.api.endpoints.getauthenticateduser;

import com.nimbleways.springboilerplate.features.users.domain.entities.User;
import com.nimbleways.springboilerplate.features.users.domain.usecases.getauthenticateduser.GetAuthenticatedUserUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GetAuthenticatedUserEndpoint {
    private static final String URL = "/auth/me";

    private final GetAuthenticatedUserUseCase getAuthenticatedUserUseCase;

    public GetAuthenticatedUserEndpoint(final GetAuthenticatedUserUseCase getAuthenticatedUserUseCase) {
        this.getAuthenticatedUserUseCase = getAuthenticatedUserUseCase;
    }

    @GetMapping(URL)
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.OK)
    public AuthenticatedUserResponse getAuthenticatedUser() {
        User user = getAuthenticatedUserUseCase.handle();
        return AuthenticatedUserResponse.from(user);
    }
}
