package com.nimbleways.springboilerplate.features.users.api.endpoints.signup;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nimbleways.springboilerplate.testhelpers.BaseWebMvcIntegrationTests;
import com.nimbleways.springboilerplate.features.users.domain.usecases.suts.SignupSut;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

@WebMvcTest(controllers = SignupEndpoint.class)
@Import(SignupSut.class)
class SignupEndpointIntegrationTests extends BaseWebMvcIntegrationTests {
    public static final String SIGNUP_ENDPOINT = "/auth/signup";

    @Autowired
    private SignupSut signupSut;

    @Test
    void returns_the_created_user_with_201_status() throws Exception {
        // WHEN
        mockMvc
            .perform(post(SIGNUP_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"name":"Name", "username":"Username",
                    "password":"password", "roles":["ADMIN"]}""")
            )

        // THEN
            .andExpect(status().isCreated())
            .andExpect(content().json(String.format("""
                    {"id":"%s","name":"Name",
                    "username":"Username","roles":["ADMIN"]}""", getUserId().toString()))
            );
    }

    private UUID getUserId() {
        return signupSut.userRepository().findAll().get(0).id();
    }
}
