package com.nimbleways.springboilerplate.testhelpers.fixtures;

import com.nimbleways.springboilerplate.common.domain.valueobjects.Email;
import com.nimbleways.springboilerplate.common.domain.valueobjects.Role;
import com.nimbleways.springboilerplate.features.authentication.domain.entities.UserPrincipal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import org.jetbrains.annotations.NotNull;

public class UserPrincipalFixture {

    @NotNull
    public static Builder aUserPrincipal() {
        return new Builder();
    }

    @NoArgsConstructor
    @With
    @AllArgsConstructor
    public static final class Builder {
        private UUID id = UUID.randomUUID();
        private String email = "email@test.com";
        private Role role = Role.USER;

        @NotNull
        public UserPrincipal build() {
            return new UserPrincipal(
                    id,
                    new Email(email),
                    role);
        }
    }
}
