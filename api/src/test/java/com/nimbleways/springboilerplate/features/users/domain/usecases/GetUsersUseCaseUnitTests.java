package com.nimbleways.springboilerplate.features.users.domain.usecases;

import static com.nimbleways.springboilerplate.testhelpers.fixtures.NewUserFixture.aNewUser;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nimbleways.springboilerplate.features.users.domain.entities.User;
import com.nimbleways.springboilerplate.testhelpers.annotations.UnitTest;
import com.nimbleways.springboilerplate.features.users.domain.usecases.suts.GetUsersSut;
import java.util.List;

import com.nimbleways.springboilerplate.testhelpers.utils.Instance;
import org.eclipse.collections.api.list.ImmutableList;
import org.junit.jupiter.api.Test;

@UnitTest
class GetUsersUseCaseUnitTests {
    private final GetUsersSut sut = Instance.create(GetUsersSut.class);

    @Test
    void returns_existing_users_in_repository() {
        // GIVEN
        User user = sut.userRepository().create(aNewUser().build());

        // WHEN
        ImmutableList<User> outputUsers = sut.handle();

        // THEN
        assertEquals(List.of(user), outputUsers);
    }
}
