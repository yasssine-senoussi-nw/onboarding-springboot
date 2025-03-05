package com.nimbleways.springboilerplate.features.users.domain.usecases.getauthenticateduser;

import com.nimbleways.springboilerplate.common.domain.valueobjects.Email;
import com.nimbleways.springboilerplate.features.users.domain.entities.User;
import com.nimbleways.springboilerplate.common.domain.ports.SecurityContextPort;
import com.nimbleways.springboilerplate.features.users.domain.ports.UserRepositoryPort;

public class GetAuthenticatedUserUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final SecurityContextPort securityContextPort;

    public GetAuthenticatedUserUseCase(UserRepositoryPort userRepositoryPort, SecurityContextPort securityContextPort) {
        this.userRepositoryPort = userRepositoryPort;
        this.securityContextPort = securityContextPort;
    }

    public User handle() {
        Email email = securityContextPort.getCurrentUserEmail().orElseThrow();
        return userRepositoryPort.findByEmail(email).orElseThrow();
    }
}
