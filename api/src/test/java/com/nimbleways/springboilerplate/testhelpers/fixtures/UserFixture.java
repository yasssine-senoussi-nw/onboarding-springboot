package com.nimbleways.springboilerplate.testhelpers.fixtures;

import com.nimbleways.springboilerplate.common.domain.ports.TimeProviderPort;
import com.nimbleways.springboilerplate.common.domain.valueobjects.Email;
import com.nimbleways.springboilerplate.common.domain.valueobjects.Role;
import com.nimbleways.springboilerplate.common.utils.collections.Immutable;
import com.nimbleways.springboilerplate.features.users.domain.entities.User;
import com.nimbleways.springboilerplate.testhelpers.configurations.TimeTestConfiguration;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import org.eclipse.collections.api.set.ImmutableSet;
import org.jetbrains.annotations.NotNull;

public class UserFixture {

    @NotNull
    public static Builder aUser() {
        return new Builder();
    }

    @NoArgsConstructor
    @With
    @AllArgsConstructor
    public static final class Builder {
        private UUID id = UUID.randomUUID();
        private String name = "name";
        private String email = "email@test.com";
        private TimeProviderPort timeProvider = TimeTestConfiguration.fixedTimeProvider();
        private ImmutableSet<Role> roles = Immutable.set.of();

        @NotNull
        public User build() {
            return new User(
                    id,
                    name,
                    new Email(email),
                    timeProvider.instant(),
                    roles);
        }
    }
}
