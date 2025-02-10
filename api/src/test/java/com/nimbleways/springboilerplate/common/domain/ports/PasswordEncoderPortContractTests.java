package com.nimbleways.springboilerplate.common.domain.ports;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nimbleways.springboilerplate.common.domain.valueobjects.EncodedPassword;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public abstract class PasswordEncoderPortContractTests {
    private PasswordEncoderPort passwordEncoder;

    @BeforeEach
    public void createSut() {
        passwordEncoder = getInstance();
    }

    @Test
    void encode_returns_an_encoded_password_not_equals_to_the_plain_one() {
        String password = "password";

        EncodedPassword encodedPassword = passwordEncoder.encode(password);

        assertNotEquals(password, encodedPassword.value());
    }

    @Test
    void matching_password_with_valid_encoded_password_returns_true() {
        String password = "password";
        EncodedPassword encodedPassword = passwordEncoder.encode(password);

        boolean isMatch = passwordEncoder.matches(password, encodedPassword);

        assertTrue(isMatch);
    }

    @Test
    void matching_password_with_different_encoded_password_returns_false() {
        String password = "password";
        EncodedPassword anotherEncodedPassword = passwordEncoder.encode("another_password");

        boolean isMatch = passwordEncoder.matches(password, anotherEncodedPassword);

        assertFalse(isMatch);
    }

    @Test
    void matching_password_with_malformed_encoded_password_returns_false() {
        String password = "password";
        EncodedPassword invalidEncodedPassword = new EncodedPassword(UUID.randomUUID().toString());

        boolean isMatch = passwordEncoder.matches(password, invalidEncodedPassword);

        assertFalse(isMatch);
    }


    // --------------------------------- Protected Methods ------------------------------- //
    protected abstract PasswordEncoderPort getInstance();
}
