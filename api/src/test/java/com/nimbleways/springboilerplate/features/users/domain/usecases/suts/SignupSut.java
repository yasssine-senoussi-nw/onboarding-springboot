package com.nimbleways.springboilerplate.features.users.domain.usecases.suts;

import com.nimbleways.springboilerplate.common.domain.ports.TimeProviderPort;
import com.nimbleways.springboilerplate.common.infra.adapters.fakes.FakePasswordEncoder;
import com.nimbleways.springboilerplate.common.infra.adapters.fakes.FakeUserRepository;
import com.nimbleways.springboilerplate.features.users.domain.entities.User;
import com.nimbleways.springboilerplate.features.users.domain.usecases.signup.SignupCommand;
import com.nimbleways.springboilerplate.features.users.domain.usecases.signup.SignupUseCase;
import com.nimbleways.springboilerplate.testhelpers.configurations.TimeTestConfiguration;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Import;

@Getter
@Import({
        SignupUseCase.class,
        FakeUserRepository.class,
        TimeTestConfiguration.class,
        FakePasswordEncoder.class
})
@RequiredArgsConstructor
public class SignupSut {
    @Getter(AccessLevel.NONE)
    private final SignupUseCase useCase;

    private final FakeUserRepository userRepository;
    private final TimeProviderPort timeProvider;
    private final FakePasswordEncoder passwordEncoder;

    public User handle(final SignupCommand signupCommand) {
        return useCase.handle(signupCommand);
    }
}
