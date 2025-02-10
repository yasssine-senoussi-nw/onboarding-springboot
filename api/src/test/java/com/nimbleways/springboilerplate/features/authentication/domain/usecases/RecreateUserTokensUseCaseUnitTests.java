package com.nimbleways.springboilerplate.features.authentication.domain.usecases;

import static com.nimbleways.springboilerplate.testhelpers.helpers.Mapper.toUserPrincipal;
import static com.nimbleways.springboilerplate.testhelpers.fixtures.NewUserFixture.aNewUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nimbleways.springboilerplate.features.authentication.domain.entities.UserSession;
import com.nimbleways.springboilerplate.features.authentication.domain.exceptions.RefreshAndAccessTokensMismatchException;
import com.nimbleways.springboilerplate.features.authentication.domain.exceptions.RefreshTokenExpiredOrNotFoundException;
import com.nimbleways.springboilerplate.features.authentication.domain.usecases.recreateusertokens.RecreateUserTokensCommand;
import com.nimbleways.springboilerplate.features.authentication.domain.valueobjects.UserTokens;
import com.nimbleways.springboilerplate.features.users.domain.entities.User;
import com.nimbleways.springboilerplate.testhelpers.annotations.UnitTest;
import com.nimbleways.springboilerplate.features.authentication.domain.usecases.suts.RecreateUserTokensSut;
import com.nimbleways.springboilerplate.features.authentication.domain.usecases.suts.RecreateUserTokensSut.TestData;
import com.nimbleways.springboilerplate.testhelpers.utils.Instance;
import org.junit.jupiter.api.Test;

@UnitTest
class RecreateUserTokensUseCaseUnitTests {

    private final RecreateUserTokensSut sut = Instance.create(RecreateUserTokensSut.class);

    @Test
    void returns_new_user_tokens_and_update_user_sessions() {
        // GIVEN
        User user = sut.userRepository().create(aNewUser().build());
        TestData testData = sut.createUserSessionAndTokens(user);
        sut.userSessionRepository().create(testData.userSession());
        RecreateUserTokensCommand recreateUserTokensCommand = new RecreateUserTokensCommand(testData.userTokens());

        // WHEN
        UserTokens newUserTokens = sut.handle(recreateUserTokensCommand);

        // THEN
        assertNotEquals(testData.userTokens().accessToken(), newUserTokens.accessToken(), "new accesstoken should differ from the previous one");
        assertEquals(sut.tokenGenerator().lastCreatedToken(), newUserTokens.accessToken(), "new accesstoken should have been created by tokenGenerator");
        assertFalse(sut.userSessionRepository().exists(testData.userTokens().refreshToken()), "previous refreshtoken should have been deleted from db");
        assertTrue(sut.userSessionRepository().exists(newUserTokens.refreshToken()), "new refreshtoken should have been inserted in db");
    }

    @Test
    void throws_RefreshTokenExpiredOrNotFoundException_when_refreshToken_not_in_repository() {
        // GIVEN
        User user = sut.userRepository().create(aNewUser().build());
        UserTokens userTokens = sut.createUserSessionAndTokens(user).userTokens();
        RecreateUserTokensCommand recreateUserTokensCommand = new RecreateUserTokensCommand(userTokens);

        // WHEN
        Exception ex = assertThrows(
            Exception.class,
            () -> sut.handle(recreateUserTokensCommand));

        //THEN
        assertEquals(RefreshTokenExpiredOrNotFoundException.class, ex.getClass());
        assertEquals("RefreshToken has expired or not present in database: " + userTokens.refreshToken().value(), ex.getMessage());
    }

    @Test
    void throws_RefreshAndAccessTokensMismatchException_when_Access_and_Refresh_tokens_belong_to_different_users() {
        // GIVEN
        User firstUser = sut.userRepository().create(aNewUser().build());
        User secondUser = sut.userRepository().create(aNewUser().build());
        UserTokens firstUserTokens = sut.createUserSessionAndTokens(firstUser).userTokens();
        UserSession userSessionSameRefreshDifferentUser = new UserSession(
            firstUserTokens.refreshToken(),
            sut.timeProvider().instant().plusSeconds(1),
            toUserPrincipal(secondUser));
        sut.userSessionRepository().create(userSessionSameRefreshDifferentUser);
        RecreateUserTokensCommand recreateUserTokensCommand = new RecreateUserTokensCommand(firstUserTokens);

        // WHEN
        Exception ex = assertThrows(
            Exception.class,
            () -> sut.handle(recreateUserTokensCommand));

        //THEN
        assertEquals(RefreshAndAccessTokensMismatchException.class, ex.getClass());
        assertEquals(String.format(
            "User ids from RefreshToken and AccessToken don't match. RefreshToken userId: %s | AccessToken userId: %s",
                secondUser.id(), firstUser.id()),
            ex.getMessage());
    }
}
