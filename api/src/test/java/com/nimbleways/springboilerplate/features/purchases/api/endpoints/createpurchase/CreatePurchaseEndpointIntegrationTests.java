package com.nimbleways.springboilerplate.features.purchases.api.endpoints.createpurchase;

import com.nimbleways.springboilerplate.features.authentication.domain.valueobjects.UserTokens;
import com.nimbleways.springboilerplate.features.purchases.domain.usecases.suts.CreatePurchaseSut;
import com.nimbleways.springboilerplate.testhelpers.BaseWebMvcIntegrationTests;
import com.nimbleways.springboilerplate.testhelpers.helpers.UserSessionHelperSut;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static com.nimbleways.springboilerplate.testhelpers.helpers.TokenHelpers.urlEncodeAccessToken;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CreatePurchaseEndpoint.class)
@Import(CreatePurchaseSut.class)
class CreatePurchaseEndpointIntegrationTests extends BaseWebMvcIntegrationTests {
    public static final String CREATE_PURCHASE_ENDPOINT = "/purchases/create";

    @Autowired
    private CreatePurchaseSut createPurchaseSut;

    @Test
    void create_and_return_same_purchase_as_command() throws Exception {
        // GIVEN
        UserSessionHelperSut.TestData testData = createPurchaseSut.sessionHelper().addUserAndSessionToRepository();
        UserTokens userTokens = testData.userTokens();
        CreatePurchaseRequest request = new CreatePurchaseRequest(
                testData.user().id().toString(),
                "brand",
                19.0
        );
        String requestJson = getRequestJson(request);

        // WHEN
        mockMvc
                .perform(
                        post(CREATE_PURCHASE_ENDPOINT)
                                .cookie(new Cookie("accessToken", urlEncodeAccessToken(userTokens)))
                                .content(requestJson)
                                .contentType("application/json")
                )

                // THEN
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(request.userId()))
                .andExpect(jsonPath("$.brand").value(request.brand()))
                .andExpect(jsonPath("$.price").value(request.price()));
    }

    @Test
    void returns_400_error_when_not_authenticated() throws Exception {
        // WHEN
        mockMvc
                .perform(post(CREATE_PURCHASE_ENDPOINT))

                // THEN
                .andExpect(status().isBadRequest());
    }

    private static String getRequestJson(CreatePurchaseRequest request) {
        return String.format("""
                {
                    "userId":"%s",
                    "brand":"%s",
                    "price":%s
                }
                """,
                request.userId(),
                request.brand(),
                request.price()
        );
    }
}
