package com.nimbleways.springboilerplate.features.users.api.endpoints.getauthenticateduser;

import com.nimbleways.springboilerplate.features.authentication.domain.valueobjects.UserTokens;
import com.nimbleways.springboilerplate.features.users.domain.entities.User;
import com.nimbleways.springboilerplate.features.users.domain.usecases.suts.GetAuthenticatedUserSut;
import com.nimbleways.springboilerplate.testhelpers.BaseWebMvcIntegrationTests;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;

import static com.nimbleways.springboilerplate.testhelpers.helpers.TokenHelpers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = GetAuthenticatedUserEndpoint.class)
@Import({GetAuthenticatedUserSut.class})
class GetAuthenticatedUserEndpointIntegrationTests extends BaseWebMvcIntegrationTests {
    public static final String GET_AUTHENTICATED_ENDPOINT = "/auth/me";

    @Autowired
    private GetAuthenticatedUserSut getAuthenticatedUserSut;

    @Test
    void returns_authenticated_user_when_get_authenticated_user_succeed() throws Exception {
        // GIVEN
        GetAuthenticatedUserSut.TestData testData = getAuthenticatedUserSut.addUserAndSessionToRepository();
        UserTokens userTokens = testData.userTokens();
        User user = testData.user();
        String exptectedJson = expectedUserContent(user);

        // WHEN
        mockMvc
                .perform(
                        get(GET_AUTHENTICATED_ENDPOINT)
                                .cookie(new Cookie("accessToken", urlEncodeAccessToken(userTokens)))
                )

                // THEN
                .andExpect(status().isOk())
                .andExpect(content().string(exptectedJson));
    }

    private String expectedUserContent(User user) {
        return String.format("{\"name\":\"%s\",\"email\":\"%s\",\"balance\":%.1f}", user.name(), user.email().value(), user.balance().value());
    }
}
