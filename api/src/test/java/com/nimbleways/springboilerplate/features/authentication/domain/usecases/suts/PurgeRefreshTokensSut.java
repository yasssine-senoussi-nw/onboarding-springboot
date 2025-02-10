package com.nimbleways.springboilerplate.features.authentication.domain.usecases.suts;

import com.nimbleways.springboilerplate.common.domain.ports.TimeProviderPort;
import com.nimbleways.springboilerplate.common.infra.adapters.fakes.FakeUserRepository;
import com.nimbleways.springboilerplate.common.infra.adapters.fakes.FakeUserSessionRepository;
import com.nimbleways.springboilerplate.features.authentication.domain.usecases.purgerefreshtokens.PurgeRefreshTokensUseCase;
import com.nimbleways.springboilerplate.testhelpers.configurations.TimeTestConfiguration;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Import;

@Getter
@Import({
        PurgeRefreshTokensUseCase.class,
        FakeUserRepository.class,
        FakeUserSessionRepository.class,
        TimeTestConfiguration.class
})
@RequiredArgsConstructor
public class PurgeRefreshTokensSut {
    @Getter(AccessLevel.NONE)
    private final PurgeRefreshTokensUseCase useCase;

    private final FakeUserRepository userRepository;
    private final FakeUserSessionRepository userSessionRepository;
    private final TimeProviderPort timeProvider;

    public void handle() {
        useCase.handle();
    }
}
