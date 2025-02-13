package com.nimbleways.springboilerplate.transverse.e2e;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nimbleways.springboilerplate.testhelpers.BaseE2ETests;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

class E2ETests extends BaseE2ETests {

    @Test
    void signup_login_refreshtoken() {
        // ----------- Sign up new user ------------//
        RequestEntity<String> request = post("/auth/signup").body("""
            {"name":"Name", "email":"email@test.com",
            "password":"password1", "role":"ADMIN"}""");
        ResponseEntity<String> response = restTemplate.exchange(request, String.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        // ----------- login ------------//
        request = post("/auth/login").body("""
            {"email":"email@test.com", "password":"password1"}""");
        response = restTemplate.exchange(request, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // ----------- refresh token ------------//
        RequestEntity<Void> voidRequestEntity = post("/auth/refreshToken").build();
        response = restTemplate.exchange(voidRequestEntity, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
