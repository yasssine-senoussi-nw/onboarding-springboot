package com.nimbleways.springboilerplate.features.users.domain.ports;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nimbleways.springboilerplate.common.domain.valueobjects.Username;
import com.nimbleways.springboilerplate.common.infra.adapters.TimeProvider;
import com.nimbleways.springboilerplate.features.authentication.domain.entities.UserCredential;
import com.nimbleways.springboilerplate.features.authentication.domain.ports.UserCredentialsRepositoryPort;
import com.nimbleways.springboilerplate.features.users.domain.entities.User;
import com.nimbleways.springboilerplate.features.users.domain.exceptions.UsernameAlreadyExistsInRepositoryException;
import com.nimbleways.springboilerplate.features.users.domain.valueobjects.NewUser;
import com.nimbleways.springboilerplate.testhelpers.fixtures.NewUserFixture;
import com.nimbleways.springboilerplate.testhelpers.fixtures.NewUserFixture.Builder;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Transactional
public abstract class UserRepositoryPortContractTests {
    private UserRepositoryPort userRepository;
    private UserCredentialsRepositoryPort userCredentialsRepositoryPort;

    @BeforeEach
    public void createSut() {
        userRepository = getUserRepository();
        userCredentialsRepositoryPort = getUserCredentialsRepository();
    }

    @Test
    void creating_a_new_user_succeed() {
        NewUser newUser = aNewUser().build();

        userRepository.create(newUser);

        NewUser newUserFromDb = reconstructNewUserFromDb(newUser.username());
        assertEquals(newUser, newUserFromDb);
    }

    @Test
    void creating_a_new_user_returns_the_created_user() {
        NewUser newUser = aNewUser().build();

        User user = userRepository.create(newUser);

        assertEquals(List.of(user), userRepository.findAll());
    }

    @Test
    void creating_a_new_user_with_an_existing_username_throws_UserAlreadyExistsInRepositoryException() {
        NewUser firstNewUser = aNewUser().withUsername(new Username("username")).build();
        NewUser secondNewUser = aNewUser().withUsername(new Username("username")).build();
        userRepository.create(firstNewUser);

        Exception exception = assertThrows(Exception.class,
            () -> userRepository.create(secondNewUser));

        assertEquals(UsernameAlreadyExistsInRepositoryException.class, exception.getClass());
        assertEquals("Username 'username' already exist in repository", exception.getMessage());
    }

    @NotNull
    private static Builder aNewUser() {
        return NewUserFixture.aNewUser().withTimeProvider(TimeProvider.UTC);
    }

    @NotNull
    private NewUser reconstructNewUserFromDb(Username username) {
        Optional<UserCredential> userCredential = userCredentialsRepositoryPort
            .findUserCredentialByUsername(username);
        assertTrue(userCredential.isPresent());
        User user = userRepository.findAll().get(0);
        return new NewUser(
            user.name(),
            user.username(),
            userCredential.get().encodedPassword(),
            user.createdAt(),
            user.roles()
        );
    }


    // --------------------------------- Protected Methods ------------------------------- //
    protected abstract UserRepositoryPort getUserRepository();
    protected abstract UserCredentialsRepositoryPort getUserCredentialsRepository();
}
