package com.nimbleways.springboilerplate.features.purchases.api.endpoints.getpurchase;

import com.nimbleways.springboilerplate.features.authentication.domain.valueobjects.UserTokens;
import com.nimbleways.springboilerplate.features.purchases.domain.entities.Purchase;
import com.nimbleways.springboilerplate.features.purchases.domain.usecases.suts.GetPurchaseSut;
import com.nimbleways.springboilerplate.testhelpers.BaseWebMvcIntegrationTests;
import com.nimbleways.springboilerplate.testhelpers.helpers.UserSessionHelperSut;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static com.nimbleways.springboilerplate.testhelpers.fixtures.NewPurchaseFixture.aNewPurchase;
import static com.nimbleways.springboilerplate.testhelpers.helpers.TokenHelpers.urlEncodeAccessToken;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = GetPurchaseEndpoint.class)
@Import({GetPurchaseSut.class})
class GetPurchaseEndPointIntegrationTests extends BaseWebMvcIntegrationTests {
    public static final String GET_PURCHASES_ENDPOINT = "/purchases/";

    @Autowired
    private GetPurchaseSut getPurchasesSut;

    @Test
    void returns_valid_purchase_when_get_purchase_succeed() throws Exception {
        // GIVEN
        UserSessionHelperSut.TestData testData = getPurchasesSut.sessionHelper().addUserAndSessionToRepository();
        UserTokens userTokens = testData.userTokens();
        Purchase purchase = getPurchasesSut.purchaseRepository().create(aNewPurchase().build(testData.user().id()));
        String expectedJson = expectedPurchases(purchase);
        UUID purchaseId = purchase.id();

        // WHEN
        mockMvc
                .perform(
                        get(GET_PURCHASES_ENDPOINT + purchaseId.toString())
                                .cookie(new Cookie("accessToken", urlEncodeAccessToken(userTokens)))
                )

                // THEN
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    void returns_403_error_when_not_authenticated() throws Exception {
        // GIVEN
        UUID purchaseId = UUID.randomUUID();

        // WHEN
        mockMvc
                .perform(
                        get(GET_PURCHASES_ENDPOINT + purchaseId.toString()))

                // THEN
                .andExpect(status().isForbidden());
    }

    private static String expectedPurchases(Purchase purchase) {
        return String.format("{\"id\":\"%s\",\"purchaseDate\":\"%s\"}", purchase.id().toString(), purchase.purchaseDate().toString());
    }
}
