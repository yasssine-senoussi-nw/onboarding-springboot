package com.nimbleways.springboilerplate.features.authentication.domain.usecases;

import static com.nimbleways.springboilerplate.testhelpers.helpers.Mapper.toUserPrincipal;
import static com.nimbleways.springboilerplate.testhelpers.fixtures.NewUserFixture.aNewUser;
import static com.nimbleways.springboilerplate.testhelpers.fixtures.SimpleFixture.aRefreshToken;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.nimbleways.springboilerplate.features.authentication.domain.entities.UserSession;
import com.nimbleways.springboilerplate.features.users.domain.entities.User;
import com.nimbleways.springboilerplate.testhelpers.annotations.UnitTest;
import com.nimbleways.springboilerplate.features.authentication.domain.usecases.suts.LogoutSut;
import com.nimbleways.springboilerplate.testhelpers.utils.Instance;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.Test;

@UnitTest
class LogoutUseCaseUnitTests {

    private final LogoutSut sut = Instance.create(LogoutSut.class);

    @Test
    void logout_deletes_session_from_repository() {
        // GIVEN
        User user = sut.userRepository().create(aNewUser().build());
        UserSession userSession = createUserSession(user);
        sut.userSessionRepository().create(userSession);

        // WHEN
        sut.handle(userSession.refreshToken());

        //THEN
        assertFalse(sut.userSessionRepository().exists(userSession.refreshToken()));
    }

    @NotNull
    private UserSession createUserSession(User user) {
        return new UserSession(
            aRefreshToken(),
            sut.timeProvider().instant().plusSeconds(5),
            toUserPrincipal(user)
        );
    }

}
