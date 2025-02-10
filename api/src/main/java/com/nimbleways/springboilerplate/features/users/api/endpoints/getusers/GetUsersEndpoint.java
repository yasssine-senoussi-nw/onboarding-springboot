package com.nimbleways.springboilerplate.features.users.api.endpoints.getusers;

import com.nimbleways.springboilerplate.features.users.domain.entities.User;
import com.nimbleways.springboilerplate.features.users.domain.usecases.getusers.GetUsersUseCase;
import org.eclipse.collections.api.list.ImmutableList;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GetUsersEndpoint {
    private static final String URL = "/users";

    private final GetUsersUseCase getUsersUseCase;

    public GetUsersEndpoint(final GetUsersUseCase getUsersUseCase) {
        this.getUsersUseCase = getUsersUseCase;
    }

    @GetMapping(URL)
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public GetUsersResponse getUsers() {
        ImmutableList<User> allUsers = getUsersUseCase.handle();
        return GetUsersResponse.from(allUsers);
    }
}
