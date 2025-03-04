package com.nimbleways.springboilerplate.testhelpers.fixtures;

import com.nimbleways.springboilerplate.common.domain.ports.EmploymentDatePort;
import com.nimbleways.springboilerplate.common.domain.ports.PasswordEncoderPort;
import com.nimbleways.springboilerplate.common.domain.ports.TimeProviderPort;
import com.nimbleways.springboilerplate.common.domain.valueobjects.Email;
import com.nimbleways.springboilerplate.common.domain.valueobjects.Role;
import com.nimbleways.springboilerplate.common.infra.adapters.fakes.FakePasswordEncoder;
import com.nimbleways.springboilerplate.features.users.domain.valueobjects.NewUser;
import com.nimbleways.springboilerplate.testhelpers.configurations.EmploymentDateTestConfiguration;
import com.nimbleways.springboilerplate.testhelpers.configurations.TimeTestConfiguration;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import org.jetbrains.annotations.NotNull;

public class NewUserFixture {

    @NotNull
    public static Builder aNewUser() {
        return new Builder();
    }

    @NoArgsConstructor
    @With
    @AllArgsConstructor
    public static final class Builder {
        private UUID id = UUID.randomUUID();
        private String name = "name-" + id;
        private Email email = new Email("email-" + id + "@test.com");
        private String plainPassword = "password-" + id;
        private Role role = Role.USER;
        private TimeProviderPort timeProvider = TimeTestConfiguration.fixedTimeProvider();
        private EmploymentDatePort employmentDateProvider = EmploymentDateTestConfiguration.fixedEmploymentDateProvider();
        private PasswordEncoderPort passwordEncoder = FakePasswordEncoder.INSTANCE;

        @NotNull
        public NewUser build() {
            return new NewUser(
                name,
                email,
                passwordEncoder.encode(plainPassword),
                timeProvider.instant(),
                employmentDateProvider.getEmploymentDate(email),
                role);
        }
    }
}
