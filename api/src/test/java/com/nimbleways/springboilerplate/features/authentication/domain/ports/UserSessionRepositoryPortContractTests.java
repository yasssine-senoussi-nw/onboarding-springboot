package com.nimbleways.springboilerplate.features.authentication.domain.ports;

import static com.nimbleways.springboilerplate.testhelpers.helpers.Mapper.toUserPrincipal;
import static com.nimbleways.springboilerplate.testhelpers.fixtures.NewUserFixture.aNewUser;
import static com.nimbleways.springboilerplate.testhelpers.fixtures.SimpleFixture.aRefreshToken;
import static com.nimbleways.springboilerplate.testhelpers.fixtures.UserPrincipalFixture.aUserPrincipal;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nimbleways.springboilerplate.common.domain.ports.TimeProviderPort;
import com.nimbleways.springboilerplate.common.domain.valueobjects.Email;
import com.nimbleways.springboilerplate.common.infra.adapters.TimeProvider;
import com.nimbleways.springboilerplate.common.utils.collections.Mutable;
import com.nimbleways.springboilerplate.features.authentication.domain.entities.UserSession;
import com.nimbleways.springboilerplate.features.authentication.domain.exceptions.CannotCreateUserSessionInRepositoryException;
import com.nimbleways.springboilerplate.features.authentication.domain.exceptions.RefreshTokenExpiredOrNotFoundException;
import com.nimbleways.springboilerplate.features.authentication.domain.valueobjects.RefreshToken;
import com.nimbleways.springboilerplate.features.users.domain.entities.User;
import com.nimbleways.springboilerplate.features.users.domain.ports.UserRepositoryPort;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.eclipse.collections.api.list.ImmutableList;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Transactional
public abstract class UserSessionRepositoryPortContractTests {
    private UserSessionRepositoryPort userSessionRepository;
    private UserRepositoryPort userRepository;
    private static final TimeProviderPort timeProvider = TimeProvider.UTC;

    @BeforeEach
    public void createSut() {
        userSessionRepository = getUserSessionRepository();
        userRepository = getUserRepository();
    }

    @Test
    void creating_a_usersession_for_a_non_existing_user_throws_CannotCreateUserSessionInRepositoryException() {
        UserSession userSession = new UserSession(aRefreshToken(), timeProvider.instant(),
            aUserPrincipal().build());

        Exception exception = assertThrows(Exception.class,
            () -> userSessionRepository.create(userSession));

        assertEquals(CannotCreateUserSessionInRepositoryException.class, exception.getClass());
        assertEquals("Cannot create UserSession in repository for email 'email@test.com'", exception.getMessage());
    }

    @Test
    void creating_a_usersession_for_an_existing_user_succeed() {
        User user = userRepository.create(aNewUser().build());
        UserSession userSession = newUserSession(user, timeProvider.instant());

        userSessionRepository.create(userSession);

        assertEquals(List.of(userSession), getAllUserSessions());
    }

    @Test
    void creating_a_usersession_with_an_existing_refreshtoken_throws_CannotCreateUserSessionInRepositoryException() {
        User user = userRepository.create(aNewUser().withEmail(new Email("email@test.com")).build());
        UserSession userSession = newUserSession(user, timeProvider.instant());
        userSessionRepository.create(userSession);

        Exception exception = assertThrows(Exception.class,
            () -> userSessionRepository.create(userSession));

        assertEquals(CannotCreateUserSessionInRepositoryException.class, exception.getClass());
        assertEquals("Cannot create UserSession in repository for email 'email@test.com'", exception.getMessage());
    }

    @Test
    void deleting_userSession_with_existing_refreshToken_succeed() {
        User user = userRepository.create(aNewUser().build());
        UserSession userSession = newUserSession(user, timeProvider.instant());
        userSessionRepository.create(userSession);

        userSessionRepository.deleteUserSessionByRefreshToken(userSession.refreshToken());

        assertEquals(0, getAllUserSessions().size());
    }

    @Test
    void deleting_userSession_with_non_existing_refreshToken_throws_RefreshTokenExpiredOrNotFoundException() {
        RefreshToken randomRefreshToken = aRefreshToken();

        Exception exception = assertThrows(Exception.class,
            () -> userSessionRepository.deleteUserSessionByRefreshToken(randomRefreshToken));

        assertEquals(RefreshTokenExpiredOrNotFoundException.class, exception.getClass());
    }

    @Test
    void finding_by_refreshToken_does_not_return_expired_sessions() {
        User user = userRepository.create(aNewUser().build());
        UserSession userSession = newUserSession(user, timeProvider.instant());
        userSessionRepository.create(userSession);

        Optional<UserSession> insertedUserSession = userSessionRepository
            .findByRefreshTokenAndExpirationDateAfter(
                userSession.refreshToken(),
                userSession.expirationDate().plusMillis(1)
            );

        assertTrue(insertedUserSession.isEmpty());
    }

    @Test
    void finding_by_refreshToken_return_valid_session() {
        User user = userRepository.create(aNewUser().build());
        UserSession userSession = newUserSession(user, timeProvider.instant());
        userSessionRepository.create(userSession);

        Optional<UserSession> insertedUserSession = userSessionRepository
            .findByRefreshTokenAndExpirationDateAfter(
                userSession.refreshToken(),
                userSession.expirationDate()
            );

        assertFalse(insertedUserSession.isEmpty());
        assertEquals(userSession, insertedUserSession.get());
    }

    @Test
    void finding_by_expiration_date_does_not_return_non_existing_session() {
        Optional<UserSession> insertedUserSession = userSessionRepository
            .findByRefreshTokenAndExpirationDateAfter(
                aRefreshToken(),
                timeProvider.instant()
            );

        assertTrue(insertedUserSession.isEmpty());
    }

    @Test
    void deleting_expired_sessions_keeps_valid_ones() {
        User user = userRepository.create(aNewUser().build());
        Instant now = timeProvider.instant();
        UserSession firstValidSession = newUserSession(user, now);
        UserSession secondValidSession = newUserSession(user, now.plusMillis(1));
        userSessionRepository.create(newUserSession(user, now.minusMillis(1)));
        userSessionRepository.create(firstValidSession);
        userSessionRepository.create(secondValidSession);

        userSessionRepository.deleteUserSessionByExpirationDateBefore(now);

        Set<UserSession> allUserSessions = Mutable.set.ofAll(getAllUserSessions());
        Set<UserSession> expectedValidUserSessions = Set.of(firstValidSession, secondValidSession);
        assertEquals(2, allUserSessions.size());
        assertEquals(expectedValidUserSessions, allUserSessions);
    }

    @NotNull
    private UserSession newUserSession(User user, Instant expirationDate) {
        return new UserSession(
            aRefreshToken(),
            expirationDate,
            toUserPrincipal(user)
        );
    }


    // --------------------------------- Protected Methods ------------------------------- //
    protected abstract UserSessionRepositoryPort getUserSessionRepository();
    protected abstract UserRepositoryPort getUserRepository();
    protected abstract ImmutableList<UserSession> getAllUserSessions();
}
