package com.nimbleways.springboilerplate.features.purchases.api.endpoints.getpurchases;

import com.nimbleways.springboilerplate.features.authentication.domain.valueobjects.UserTokens;
import com.nimbleways.springboilerplate.features.purchases.domain.entities.Purchase;
import com.nimbleways.springboilerplate.features.purchases.domain.usecases.suts.GetPurchasesSut;
import com.nimbleways.springboilerplate.features.purchases.domain.valueobjects.NewPurchase;
import com.nimbleways.springboilerplate.testhelpers.BaseWebMvcIntegrationTests;
import com.nimbleways.springboilerplate.testhelpers.helpers.UserSessionHelperSut;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;

import java.time.Instant;

import static com.nimbleways.springboilerplate.testhelpers.helpers.TokenHelpers.urlEncodeAccessToken;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = GetPurchasesEndpoint.class)
@Import({GetPurchasesSut.class})
class GetPurchasesEndPointIntegrationTests extends BaseWebMvcIntegrationTests {
    public static final String GET_PURCHASES_ENDPOINT = "/purchases/get";

    @Autowired
    private GetPurchasesSut getPurchasesSut;

    @Test
    void returns_empty_purchases_when_get_purchases_succeed() throws Exception {
        // GIVEN
        UserSessionHelperSut.TestData testData = getPurchasesSut.sessionHelper().addUserAndSessionToRepository();
        UserTokens userTokens = testData.userTokens();
        String expectedJson = "[]";

        // WHEN
        mockMvc
                .perform(
                        get(GET_PURCHASES_ENDPOINT)
                                .cookie(new Cookie("accessToken", urlEncodeAccessToken(userTokens)))
                )

                // THEN
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    void returns_nonempty_purchases_when_get_purchases_succeed() throws Exception {
        // GIVEN
        UserSessionHelperSut.TestData testData = getPurchasesSut.sessionHelper().addUserAndSessionToRepository();
        Purchase purchase = getPurchasesSut.purchaseRepository().create(new NewPurchase(testData.user().id(), Instant.now()));
        UserTokens userTokens = testData.userTokens();
        String expectedJson = expectedPurchases(purchase);

        // WHEN
        mockMvc
                .perform(
                        get(GET_PURCHASES_ENDPOINT)
                                .cookie(new Cookie("accessToken", urlEncodeAccessToken(userTokens)))
                )

                // THEN
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    void returns_403_error_when_not_authenticated() throws Exception {
        // WHEN
        mockMvc
                .perform(get(GET_PURCHASES_ENDPOINT))

                // THEN
                .andExpect(status().isForbidden());
    }

    private static String expectedPurchases(Purchase purchase) {
        return String.format("[{\"id\":\"%s\",\"purchaseDate\":\"%s\"}]", purchase.id().toString(), purchase.purchaseDate().toString());
    }
}
