package com.nimbleways.springboilerplate.features.authentication.api.endpoints.recreateusertokens;

import static com.nimbleways.springboilerplate.testhelpers.helpers.TokenHelpers.urlEncode;
import static com.nimbleways.springboilerplate.testhelpers.helpers.TokenHelpers.urlEncodeAccessToken;
import static com.nimbleways.springboilerplate.testhelpers.helpers.TokenHelpers.urlEncodeRefreshToken;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nimbleways.springboilerplate.features.authentication.domain.valueobjects.UserTokens;
import com.nimbleways.springboilerplate.testhelpers.BaseWebMvcIntegrationTests;
import com.nimbleways.springboilerplate.features.authentication.domain.usecases.suts.RecreateUserTokensSut;
import com.nimbleways.springboilerplate.features.authentication.domain.usecases.suts.RecreateUserTokensSut.TestData;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

@WebMvcTest(controllers = RecreateUserTokensEndpoint.class)
@Import(RecreateUserTokensSut.class)
class RecreateUserTokensEndpointIntegrationTests extends BaseWebMvcIntegrationTests {
    private static final String uri = "/auth/refreshToken";

    @Autowired
    private RecreateUserTokensSut recreateUserTokensSut;

    @Test
    void returns_new_AccessToken_and_RefreshToken_in_cookies() throws Exception {
        //GIVEN
        TestData testData = recreateUserTokensSut.addUserAndSessionToRepository();
        UserTokens userTokens = testData.userTokens();

        //WHEN
        mockMvc
            .perform(
                post(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .cookie(new Cookie("accessToken", urlEncodeAccessToken(userTokens)))
                    .cookie(new Cookie("refreshToken", urlEncodeRefreshToken(userTokens)))
            )

        //THEN
            .andExpect(status().isOk())
            .andExpect(cookie().value("accessToken", urlEncodeLastCreatedToken()))
            .andExpect(cookie().value("refreshToken", urlEncode("cfcd2084-95d5-35ef-a6e7-dff9f98764da")))
            .andExpect(content().string(""));
    }

    private String urlEncodeLastCreatedToken() {
        return urlEncode(recreateUserTokensSut.tokenGenerator().lastCreatedToken().value());
    }
}
