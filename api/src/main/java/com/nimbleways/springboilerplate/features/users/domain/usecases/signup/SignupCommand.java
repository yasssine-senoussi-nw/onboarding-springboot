package com.nimbleways.springboilerplate.features.users.domain.usecases.signup;

import com.nimbleways.springboilerplate.common.domain.valueobjects.Email;
import com.nimbleways.springboilerplate.common.domain.valueobjects.EncodedPassword;
import com.nimbleways.springboilerplate.common.domain.valueobjects.Role;
import com.nimbleways.springboilerplate.features.users.domain.valueobjects.NewUser;
import java.time.Instant;
import org.eclipse.collections.api.set.ImmutableSet;

public record SignupCommand(
        String name,
        Email email,
        String plainPassword,
        ImmutableSet<Role> roles
) {
    public NewUser toNewUser(EncodedPassword encodedPassword, Instant creationDateTime) {
        return new NewUser(
                name(),
                email(),
                encodedPassword,
                creationDateTime,
                roles()
        );
    }
}