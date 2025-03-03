package com.nimbleways.springboilerplate.features.users.domain.ports;

import com.nimbleways.springboilerplate.common.domain.valueobjects.Email;
import com.nimbleways.springboilerplate.features.users.domain.entities.User;
import com.nimbleways.springboilerplate.features.users.domain.valueobjects.NewUser;
import org.eclipse.collections.api.list.ImmutableList;

import java.util.Optional;

public interface UserRepositoryPort {
    User create(NewUser userToCreate);

    ImmutableList<User> findAll();

    Optional<User> findByEmail(Email email);
}
