package com.nimbleways.springboilerplate.features.users.domain.usecases.signup;

import com.nimbleways.springboilerplate.common.domain.valueobjects.Email;
import com.nimbleways.springboilerplate.common.domain.valueobjects.EncodedPassword;
import com.nimbleways.springboilerplate.common.domain.valueobjects.Role;
import com.nimbleways.springboilerplate.features.users.domain.valueobjects.NewUser;
import java.time.Instant;

public record SignupCommand(
        String name,
        Email email,
        String plainPassword,
        Role role
) {
    public NewUser toNewUser(EncodedPassword encodedPassword, Instant creationDateTime) {
        return new NewUser(
                name(),
                email(),
                encodedPassword,
                creationDateTime,
                role()
        );
    }
}