package com.nimbleways.springboilerplate.features.users.api.endpoints.getusers;

import static com.nimbleways.springboilerplate.testhelpers.fixtures.NewUserFixture.aNewUser;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nimbleways.springboilerplate.common.domain.valueobjects.Role;
import com.nimbleways.springboilerplate.common.domain.valueobjects.Username;
import com.nimbleways.springboilerplate.common.utils.collections.Immutable;
import com.nimbleways.springboilerplate.features.users.domain.entities.User;
import com.nimbleways.springboilerplate.testhelpers.BaseWebMvcIntegrationTests;
import com.nimbleways.springboilerplate.features.users.domain.usecases.suts.GetUsersSut;
import org.eclipse.collections.api.set.ImmutableSet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;

@WebMvcTest(controllers = GetUsersEndpoint.class)
@Import(GetUsersSut.class)
class GetUsersEndpointIntegrationTests extends BaseWebMvcIntegrationTests {
    public static final String GET_USERS_ENDPOINT = "/users";

    @Autowired
    private GetUsersSut getUsersSut;

    @Test
    void testGetUsersEndpoint() throws Exception {
        // GIVEN
        User user1 = getUser("user1", "username1", Immutable.set.of(Role.ADMIN));
        User user2 = getUser("user2", "username2", Immutable.set.of());

        // WHEN
        mockMvc
            .perform(get(GET_USERS_ENDPOINT)
                .cookie(getAccessTokenCookie(user1))
            )

        // THEN
            .andExpect(status().isOk())
            .andExpect(content().json(String.format("""
                [{"id":"%s","username":"username1","name":"user1"},
                {"id":"%s","username":"username2","name":"user2"}]""",
                user1.id().toString(), user2.id().toString())));
    }

    @Test
    void testGetUsersEndpointShouldReturn403() throws Exception {
        // WHEN
        mockMvc
            .perform(get(GET_USERS_ENDPOINT))

        // THEN
            .andExpect(status().isForbidden());
    }

    private User getUser(String name, String username, ImmutableSet<Role> roles) {
        return getUsersSut.userRepository().create(
            aNewUser()
                .withName(name)
                .withUsername(new Username(username))
                .withRoles(roles)
                .build()
        );
    }
}
