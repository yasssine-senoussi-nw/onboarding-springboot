package com.nimbleways.springboilerplate.features.authentication.domain.usecases.suts;

import com.nimbleways.springboilerplate.common.domain.ports.TimeProviderPort;
import com.nimbleways.springboilerplate.common.infra.adapters.fakes.FakeUserRepository;
import com.nimbleways.springboilerplate.common.infra.adapters.fakes.FakeUserSessionRepository;
import com.nimbleways.springboilerplate.features.authentication.domain.usecases.logout.LogoutUseCase;
import com.nimbleways.springboilerplate.features.authentication.domain.valueobjects.RefreshToken;
import com.nimbleways.springboilerplate.features.authentication.domain.valueobjects.UserTokens;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Import;

@Getter
@Import({
        LogoutUseCase.class,
        RecreateUserTokensSut.class
})
@RequiredArgsConstructor
public class LogoutSut {
    @Getter(AccessLevel.NONE)
    private final LogoutUseCase useCase;

    private final RecreateUserTokensSut recreateUserTokensSut;

    public UserTokens addUserAndSessionToRepository() {
        return recreateUserTokensSut().addUserAndSessionToRepository().userTokens();
    }

    public FakeUserRepository userRepository() {
        return recreateUserTokensSut.userRepository();
    }

    public FakeUserSessionRepository userSessionRepository() {
        return recreateUserTokensSut.userSessionRepository();
    }

    public TimeProviderPort timeProvider() {
        return recreateUserTokensSut().timeProvider();
    }


    public void handle(final RefreshToken refreshToken) {
        useCase.handle(refreshToken);
    }
}
