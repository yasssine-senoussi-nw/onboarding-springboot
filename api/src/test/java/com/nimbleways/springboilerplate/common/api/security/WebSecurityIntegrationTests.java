package com.nimbleways.springboilerplate.common.api.security;

import static com.nimbleways.springboilerplate.common.api.security.SecurityFakeEndpoint.PERMIT_ALL_POST_ENDPOINT;
import static com.nimbleways.springboilerplate.testhelpers.helpers.TokenHelpers.urlEncode;
import static com.nimbleways.springboilerplate.testhelpers.fixtures.UserPrincipalFixture.aUserPrincipal;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nimbleways.springboilerplate.common.domain.ports.TimeProviderPort;
import com.nimbleways.springboilerplate.features.authentication.domain.entities.TokenClaims;
import com.nimbleways.springboilerplate.features.authentication.domain.ports.TokenClaimsCodecPort;
import com.nimbleways.springboilerplate.features.authentication.domain.valueobjects.AccessToken;
import com.nimbleways.springboilerplate.testhelpers.BaseWebMvcIntegrationTests;
import jakarta.servlet.http.Cookie;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

@WebMvcTest({SecurityFakeEndpoint.class})
class WebSecurityIntegrationTests extends BaseWebMvcIntegrationTests {
    @Autowired
    private TokenClaimsCodecPort tokenClaimsCodec;
    @Autowired
    private TimeProviderPort timeProvider;

    @Test
    void can_access_permit_all_endpoints_with_expired_token() throws Exception {
        //GIVEN
        AccessToken expiredAccessToken = getExpiredAccessToken();

        // WHEN
        mockMvc
            .perform(post(PERMIT_ALL_POST_ENDPOINT)
                .cookie(new Cookie("accessToken", urlEncode(expiredAccessToken.value()))))

        // THEN
            .andExpect(status().isOk());
    }

    @Test
    void preflight_request_with_allowed_origin_succeed() throws Exception {
        // GIVEN
        String localDevFrontendUrl = "http://localhost:3000";

        // WHEN
        mockMvc.perform(
                options(PERMIT_ALL_POST_ENDPOINT)
				    // Value of 'Origin' header should be the same as the one specified in the 'addAllowedOrigin' method of the CorsConfiguration class
				    // Or, if you are using the 'addAllowedOriginPattern' method, the value of 'Origin' header should match the pattern specified in
                    // the 'addAllowedOriginPattern' method of the CorsConfiguration class
                    .header("Origin", localDevFrontendUrl)
                    .header("Access-Control-Request-Method", "POST")
            )

        // THEN
            .andExpect(status().isOk());
    }

    private AccessToken getExpiredAccessToken() {
        Instant now = timeProvider.instant();
        return tokenClaimsCodec.encode(
            new TokenClaims(
                aUserPrincipal().build(),
                now.minusSeconds(20),
                now.minusSeconds(10)
            )
        );
    }
}


