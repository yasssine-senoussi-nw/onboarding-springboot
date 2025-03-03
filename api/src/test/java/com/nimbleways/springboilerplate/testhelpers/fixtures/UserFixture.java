package com.nimbleways.springboilerplate.testhelpers.fixtures;

import com.nimbleways.springboilerplate.common.domain.ports.EmploymentDatePort;
import com.nimbleways.springboilerplate.common.domain.ports.TimeProviderPort;
import com.nimbleways.springboilerplate.common.domain.valueobjects.Email;
import com.nimbleways.springboilerplate.common.domain.valueobjects.Role;
import com.nimbleways.springboilerplate.features.users.domain.entities.User;
import com.nimbleways.springboilerplate.testhelpers.configurations.EmploymentDateTestConfiguration;
import com.nimbleways.springboilerplate.testhelpers.configurations.TimeTestConfiguration;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
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
        private EmploymentDatePort employmentDateProvider = EmploymentDateTestConfiguration.fixedEmploymentDateProvider();
        private Role role = Role.USER;

        @NotNull
        public User build() {
            Email userEmail = new Email(email);
            return new User(
                    id,
                    name,
                    userEmail,
                    timeProvider.instant(),
                    employmentDateProvider.getEmploymentDate(userEmail),
                    role);
        }
    }
}
