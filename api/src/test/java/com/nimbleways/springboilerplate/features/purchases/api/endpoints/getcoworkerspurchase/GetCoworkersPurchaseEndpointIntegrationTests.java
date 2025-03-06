package com.nimbleways.springboilerplate.features.purchases.api.endpoints.getcoworkerspurchase;

import com.nimbleways.springboilerplate.features.purchases.domain.entities.Purchase;
import com.nimbleways.springboilerplate.features.purchases.domain.usecases.suts.GetCoworkersPurchaseSut;
import com.nimbleways.springboilerplate.testhelpers.BaseWebMvcIntegrationTests;
import com.nimbleways.springboilerplate.testhelpers.helpers.UserSessionHelperSut;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.ResultActions;

import java.util.UUID;

import static com.nimbleways.springboilerplate.testhelpers.fixtures.NewPurchaseFixture.aNewPurchase;
import static com.nimbleways.springboilerplate.testhelpers.helpers.TokenHelpers.urlEncodeAccessToken;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = GetCoworkersPurchaseEndpoint.class)
@Import(GetCoworkersPurchaseSut.class)
class GetCoworkersPurchaseEndpointIntegrationTests extends BaseWebMvcIntegrationTests {
    public static final String GET_COWORKERS_PURCHASE_ENDPOINT = "/purchases/getcoworkers";

    @Autowired
    private GetCoworkersPurchaseSut sut;

    @Test
    void returns_nonempty_purchases_when_get_purchases_succeed() throws Exception {
        // GIVEN
        UserSessionHelperSut.TestData testData1 = sut.sessionHelper().addUserAndSessionToRepository();
        UserSessionHelperSut.TestData testData2 = sut.sessionHelper().addUserAndSessionToRepository();

        Purchase purchase1 = sut.purchaseRepository().create(aNewPurchase().build(testData1.user().id()));
        Purchase purchase2 = sut.purchaseRepository().create(aNewPurchase().build(testData2.user().id()));

        // WHEN
        ResultActions query1 = mockMvc
            .perform(
                get(GET_COWORKERS_PURCHASE_ENDPOINT)
                    .cookie(new Cookie("accessToken", urlEncodeAccessToken(testData1.userTokens())))
                    .contentType("application/json")
                    .content(getRequestJson(testData1.user().id()))
            );

        ResultActions query2 = mockMvc
            .perform(
                get(GET_COWORKERS_PURCHASE_ENDPOINT)
                    .cookie(new Cookie("accessToken", urlEncodeAccessToken(testData2.userTokens())))
                    .contentType("application/json")
                    .content(getRequestJson(testData2.user().id()))
            );


        // THEN
        query1
            .andExpect(status().isOk())
            .andExpect(content().json(getResponseJson(purchase2)));

        query2
            .andExpect(status().isOk())
            .andExpect(content().json(getResponseJson(purchase1)));
    }


    @Test
    void returns_403_error_when_not_authenticated() throws Exception {
        // WHEN
        mockMvc
            .perform(
                get(GET_COWORKERS_PURCHASE_ENDPOINT)
                    .contentType("application/json")
                    .content(getRequestJson(UUID.randomUUID()))
            )

            // THEN
            .andExpect(status().isForbidden());
    }

    private static String getRequestJson(UUID userId) {
        return """
            {
                "userId": "%s"
            }
            """.formatted(userId);
    }

    private static String getResponseJson(Purchase purchase) {
        String images = purchase.images().collect(image -> "\"" + image + "\"").makeString("[", ",", "]");
        return """
            [{
                "id": "%s",
                "purchaseDate": "%s",
                "name": "%s",
                "brand": "%s",
                "model": "%s",
                "store": "%s",
                "images": %s,
                "price": %s,
                "rating": %s
            }]
            """.formatted(
            purchase.id().toString(),
            purchase.purchaseDate().toString(),
            purchase.name(),
            purchase.brand(),
            purchase.model(),
            purchase.store(),
            images,
            purchase.price().value(),
            purchase.rating().value()
        );
    }
}
