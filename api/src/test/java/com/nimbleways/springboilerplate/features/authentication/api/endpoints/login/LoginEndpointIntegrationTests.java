package com.nimbleways.springboilerplate.features.authentication.api.endpoints.login;

import static com.nimbleways.springboilerplate.testhelpers.helpers.TokenHelpers.urlEncode;
import static com.nimbleways.springboilerplate.testhelpers.fixtures.NewUserFixture.aNewUser;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nimbleways.springboilerplate.common.domain.valueobjects.Email;
import com.nimbleways.springboilerplate.testhelpers.BaseWebMvcIntegrationTests;
import com.nimbleways.springboilerplate.features.authentication.domain.usecases.suts.LoginSut;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

@WebMvcTest(controllers = LoginEndpoint.class)
@Import(LoginSut.class)
class LoginEndpointIntegrationTests extends BaseWebMvcIntegrationTests {
    public static final String LOGIN_ENDPOINT = "/auth/login";

    @Autowired
    private LoginSut loginSut;

    @Test
    void returns_AccessToken_and_RefreshToken_in_cookies() throws Exception {
        // GIVEN
        loginSut.userRepository().create(
            aNewUser()
                .withEmail(new Email("email@test.com"))
                .withPlainPassword("passwordCreated")
                .build()
        );

        // WHEN
        mockMvc
            .perform(
                post(LOGIN_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"email":"email@test.com", "password":"passwordCreated"}""")
            )

        // THEN
            .andExpect(status().isOk())
            .andExpect(cookie().value("accessToken", urlEncodeLastCreatedToken()))
            .andExpect(cookie().value("refreshToken", urlEncode("cfcd2084-95d5-35ef-a6e7-dff9f98764da")))
            .andExpect(content().string(""));
    }

    private String urlEncodeLastCreatedToken() {
        return urlEncode(loginSut.tokenGenerator().lastCreatedToken().value());
    }
}
