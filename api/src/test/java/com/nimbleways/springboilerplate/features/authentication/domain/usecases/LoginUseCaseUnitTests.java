package com.nimbleways.springboilerplate.features.authentication.domain.usecases;

import static com.nimbleways.springboilerplate.testhelpers.helpers.Mapper.toUserPrincipal;
import static com.nimbleways.springboilerplate.testhelpers.fixtures.NewUserFixture.aNewUser;
import static com.nimbleways.springboilerplate.testhelpers.fixtures.SimpleFixture.aLoginCommand;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nimbleways.springboilerplate.features.authentication.domain.entities.TokenClaims;
import com.nimbleways.springboilerplate.features.authentication.domain.entities.UserSession;
import com.nimbleways.springboilerplate.features.authentication.domain.events.UserLoggedInEvent;
import com.nimbleways.springboilerplate.features.authentication.domain.exceptions.BadUserCredentialException;
import com.nimbleways.springboilerplate.features.authentication.domain.exceptions.UnknownEmailException;
import com.nimbleways.springboilerplate.features.authentication.domain.usecases.login.LoginCommand;
import com.nimbleways.springboilerplate.features.authentication.domain.valueobjects.UserTokens;
import com.nimbleways.springboilerplate.features.users.domain.entities.User;
import com.nimbleways.springboilerplate.features.users.domain.valueobjects.NewUser;
import com.nimbleways.springboilerplate.testhelpers.annotations.UnitTest;
import com.nimbleways.springboilerplate.features.authentication.domain.usecases.suts.LoginSut;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.nimbleways.springboilerplate.testhelpers.utils.Instance;
import org.eclipse.collections.api.list.ImmutableList;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

@UnitTest
class LoginUseCaseUnitTests {

    private final LoginSut sut = Instance.create(LoginSut.class);

    @Test
    void returns_user_tokens_when_email_password_matches() {
        // GIVEN
        LoginCommand loginCommand = aLoginCommand();
        User user = sut.userRepository().create(getUser(loginCommand));

        // WHEN
        UserTokens response = sut.handle(loginCommand);

        // THEN
        TokenClaims claims = sut.tokenGenerator().decodeWithoutExpirationValidation(response.accessToken());
        assertEquals(toUserPrincipal(user), claims.userPrincipal());
        Instant expectedExpirationTime = sut.timeProvider().instant().plus(sut.tokenProperties().accessTokenValidityDuration());
        assertEquals(expectedExpirationTime, claims.expirationTime());
    }

    @Test
    void returns_new_user_tokens_each_time() {
        // GIVEN
        LoginCommand loginCommand = aLoginCommand();
        sut.userRepository().create(getUser(loginCommand));

        // WHEN
        UserTokens firstUserTokens = sut.handle(loginCommand);
        UserTokens secondUserTokens = sut.handle(loginCommand);

        // THEN
        assertNotEquals(firstUserTokens.accessToken(), secondUserTokens.accessToken());
        assertNotEquals(firstUserTokens.refreshToken(), secondUserTokens.refreshToken());
    }

    @Test
    void publish_UserLoggedInEvent_on_success() {
        // GIVEN
        LoginCommand loginCommand = aLoginCommand();
        User user = sut.userRepository().create(getUser(loginCommand));

        // WHEN
        sut.handle(loginCommand);

        // THEN
        Optional<UserLoggedInEvent> lastEvent = sut.eventPublisher().lastEvent(UserLoggedInEvent.class);
        assertTrue(lastEvent.isPresent());
        assertEquals(toUserPrincipal(user), lastEvent.get().userPrincipal());
        assertNotNull(lastEvent.get().sourceType());
        assertEquals("Login attempt successful for user with id: " + user.id(), lastEvent.get().toString());
    }

    @Test
    void returns_a_refresh_token_with_expected_duration() {
        // GIVEN
        LoginCommand loginCommand = aLoginCommand();
        User user = sut.userRepository().create(getUser(loginCommand));

        // WHEN
        UserTokens userTokens = sut.handle(loginCommand);

        // THEN
        UserSession expectedUserSession = new UserSession(
            userTokens.refreshToken(),
            sut.timeProvider().instant().plus(sut.tokenProperties().refreshTokenValidityDuration()),
            toUserPrincipal(user));
        ImmutableList<UserSession> userSessions = sut.userSessionRepository().findAll();
        assertEquals(List.of(expectedUserSession), userSessions);

    }

    @Test
    void throws_UnknownEmailException_if_email_not_in_repository() {
        // GIVEN
        LoginCommand loginCommand = aLoginCommand();

        // WHEN
        Exception ex = assertThrows(Exception.class, () -> sut.handle(loginCommand));

        // THEN
        assertEquals(UnknownEmailException.class, ex.getClass());
        assertEquals("Email not found: email@test.com", ex.getMessage());
    }

    @Test
    void throws_BadUserCredentialException_if_wrong_password() {
        // GIVEN
        LoginCommand loginCommand = aLoginCommand("bad_password");
        sut.userRepository().create(getUser(loginCommand, "password"));

        // WHEN
        Exception ex = assertThrows(Exception.class, () -> sut.handle(loginCommand));

        // THEN
        assertEquals(BadUserCredentialException.class, ex.getClass());
        assertEquals("Bad password provided for email: email@test.com", ex.getMessage());
    }

    @NotNull
    private static NewUser getUser(LoginCommand loginCommand, String password) {
        return aNewUser()
            .withEmail(loginCommand.email())
            .withPlainPassword(password)
            .build();
    }

    @NotNull
    private static NewUser getUser(LoginCommand loginCommand) {
        return getUser(loginCommand, loginCommand.password());
    }

}
