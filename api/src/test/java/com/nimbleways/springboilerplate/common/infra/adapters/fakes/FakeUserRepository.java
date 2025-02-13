package com.nimbleways.springboilerplate.common.infra.adapters.fakes;

import static com.nimbleways.springboilerplate.testhelpers.helpers.Mapper.toUserPrincipal;

import com.nimbleways.springboilerplate.common.domain.valueobjects.Email;
import com.nimbleways.springboilerplate.common.domain.valueobjects.EncodedPassword;
import com.nimbleways.springboilerplate.common.utils.collections.Mutable;
import com.nimbleways.springboilerplate.features.authentication.domain.entities.UserCredential;
import com.nimbleways.springboilerplate.features.authentication.domain.ports.UserCredentialsRepositoryPort;
import com.nimbleways.springboilerplate.features.users.domain.entities.User;
import com.nimbleways.springboilerplate.features.users.domain.exceptions.EmailAlreadyExistsInRepositoryException;
import com.nimbleways.springboilerplate.features.users.domain.ports.UserRepositoryPort;
import com.nimbleways.springboilerplate.features.users.domain.valueobjects.NewUser;
import java.util.Optional;
import java.util.UUID;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.springframework.dao.DataIntegrityViolationException;

public class FakeUserRepository implements UserRepositoryPort, UserCredentialsRepositoryPort {

    private final MutableMap<Email, UserWithPassword> userTable = Mutable.map.empty();

    @Override
    public User create(NewUser userToCreate) {
        if (userTable.containsKey(userToCreate.email())) {
            throw new EmailAlreadyExistsInRepositoryException(
                userToCreate.email(), new DataIntegrityViolationException(""));
        }
        User user = new User(UUID.randomUUID(), userToCreate.name(), userToCreate.email(),
            userToCreate.creationDateTime(),
            userToCreate.role());
        userTable.put(user.email(), new UserWithPassword(user, userToCreate.encodedPassword()));
        return user;
    }

    @Override
    public ImmutableList<User> findAll() {
        return userTable.collect(UserWithPassword::user).toImmutableList();
    }

    @Override
    public Optional<UserCredential> findUserCredentialByEmail(Email email) {
        return Optional
            .ofNullable(userTable.get(email))
            .map(u -> new UserCredential(toUserPrincipal(u.user()), u.encodedPassword()));
    }

    public boolean userIdExists(UUID userId) {
        return userTable.values().stream().anyMatch(u -> u.user().id().equals(userId));
    }

    private record UserWithPassword(User user, EncodedPassword encodedPassword) {}
}
