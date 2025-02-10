package com.nimbleways.springboilerplate.features.authentication.domain.usecases.suts;

import com.nimbleways.springboilerplate.common.domain.ports.TimeProviderPort;
import com.nimbleways.springboilerplate.common.infra.adapters.fakes.*;
import com.nimbleways.springboilerplate.features.authentication.domain.properties.TokenProperties;
import com.nimbleways.springboilerplate.features.authentication.domain.usecases.login.LoginCommand;
import com.nimbleways.springboilerplate.features.authentication.domain.usecases.login.LoginUseCase;
import com.nimbleways.springboilerplate.features.authentication.domain.valueobjects.UserTokens;
import com.nimbleways.springboilerplate.testhelpers.configurations.PropertiesTestConfiguration;
import com.nimbleways.springboilerplate.testhelpers.configurations.TimeTestConfiguration;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Import;

@Getter
@Import({
        LoginUseCase.class,
        FakeUserSessionRepository.class,
        FakeUserRepository.class,
        FakeTokenClaimsCodec.class,
        PropertiesTestConfiguration.class,
        TimeTestConfiguration.class,
        FakeEventPublisher.class,
        FakePasswordEncoder.class,
        FakeRandomGenerator.class
})
@RequiredArgsConstructor
public class LoginSut {
    @Getter(AccessLevel.NONE)
    private final LoginUseCase useCase;

    private final FakeUserRepository userRepository;
    private final FakeTokenClaimsCodec tokenGenerator;
    private final TokenProperties tokenProperties;
    private final TimeProviderPort timeProvider;
    private final FakeEventPublisher eventPublisher;
    private final FakeUserSessionRepository userSessionRepository;

    public UserTokens handle(final LoginCommand loginCommand) {
        return useCase.handle(loginCommand);
    }
}
