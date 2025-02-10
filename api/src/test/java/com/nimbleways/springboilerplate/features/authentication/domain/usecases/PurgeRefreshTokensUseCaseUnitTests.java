package com.nimbleways.springboilerplate.features.authentication.domain.usecases;

import static com.nimbleways.springboilerplate.testhelpers.helpers.Mapper.toUserPrincipal;
import static com.nimbleways.springboilerplate.testhelpers.fixtures.NewUserFixture.aNewUser;
import static com.nimbleways.springboilerplate.testhelpers.fixtures.SimpleFixture.aRefreshToken;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nimbleways.springboilerplate.features.authentication.domain.entities.UserSession;
import com.nimbleways.springboilerplate.features.authentication.domain.usecases.suts.PurgeRefreshTokensSut;
import com.nimbleways.springboilerplate.features.users.domain.entities.User;
import com.nimbleways.springboilerplate.testhelpers.annotations.UnitTest;
import java.time.Instant;

import com.nimbleways.springboilerplate.testhelpers.utils.Instance;
import org.eclipse.collections.api.list.ImmutableList;
import org.junit.jupiter.api.Test;

@UnitTest
class PurgeRefreshTokensUseCaseUnitTests {

    private final PurgeRefreshTokensSut sut = Instance.create(PurgeRefreshTokensSut.class);

    @Test
    void deletes_all_expired_refreshTokens() {
        // GIVEN
        User user = sut.userRepository().create(aNewUser().build());
        Instant now = sut.timeProvider().instant();
        Instant futureExpirationDate = now.plusMillis(1);
        addSession(user, futureExpirationDate);
        addSession(user, now.minusMillis(1));
        addSession(user, now.minusMillis(2));

        // WHEN
        sut.handle();

        //ASSERT
        ImmutableList<UserSession> sessions = sut.userSessionRepository().findAll();
        assertEquals(1, sessions.size());
        assertEquals(futureExpirationDate, sessions.get(0).expirationDate());
    }

    private void addSession(User user, Instant expirationDate) {
        UserSession userSession = new UserSession(
            aRefreshToken(),
            expirationDate,
            toUserPrincipal(user)
        );
        sut.userSessionRepository().create(userSession);
    }
}