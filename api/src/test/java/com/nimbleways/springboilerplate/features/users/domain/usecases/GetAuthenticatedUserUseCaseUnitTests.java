package com.nimbleways.springboilerplate.features.users.domain.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nimbleways.springboilerplate.features.users.domain.entities.User;
import com.nimbleways.springboilerplate.features.users.domain.usecases.suts.GetAuthenticatedUserSut;
import com.nimbleways.springboilerplate.testhelpers.annotations.UnitTest;
import com.nimbleways.springboilerplate.testhelpers.utils.Instance;
import org.junit.jupiter.api.Test;

@UnitTest
class GetAuthenticatedUserUseCaseUnitTests {
    private final GetAuthenticatedUserSut sut = Instance.create(GetAuthenticatedUserSut.class);

    @Test
    void returns_authenticated_user() {
        // GIVEN
        User user = sut.addUserAndSessionToRepository().user();

        // WHEN
        User result = sut.handle();

        // THEN
        assertEquals(user, result);
    }
}
