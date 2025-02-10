package com.nimbleways.springboilerplate.features.authentication.api.endpoints.logout;

import static com.nimbleways.springboilerplate.testhelpers.helpers.TokenHelpers.urlEncodeAccessToken;
import static com.nimbleways.springboilerplate.testhelpers.helpers.TokenHelpers.urlEncodeRefreshToken;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nimbleways.springboilerplate.features.authentication.domain.valueobjects.UserTokens;
import com.nimbleways.springboilerplate.testhelpers.BaseWebMvcIntegrationTests;
import com.nimbleways.springboilerplate.features.authentication.domain.usecases.suts.LogoutSut;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;


@WebMvcTest(controllers = LogoutEndpoint.class)
@Import(LogoutSut.class)
class LogoutEndpointIntegrationTests extends BaseWebMvcIntegrationTests {
    private static final String uri = "/auth/logout";

    @Autowired
    private LogoutSut logoutSut;

    @Test
    void returns_expired_cookies() throws Exception {
        //GIVEN
        UserTokens userTokens = logoutSut.addUserAndSessionToRepository();

        // WHEN
        mockMvc
            .perform(
                post(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .cookie(new Cookie("accessToken", urlEncodeAccessToken(userTokens)))
                    .cookie(new Cookie("refreshToken", urlEncodeRefreshToken(userTokens)))
            )

        // THEN
            .andExpect(status().isOk())
            .andExpect(cookie().maxAge("accessToken", 0))
            .andExpect(cookie().maxAge("refreshToken", 0))
            .andExpect(content().string(""));
    }

    @Test
    void returns_forbidden_when_not_authenticated() throws Exception {
        // WHEN
        mockMvc
            .perform(
                post(uri)
                    .contentType(MediaType.APPLICATION_JSON)
            )

        // THEN
            .andExpect(status().isForbidden());
    }

}
