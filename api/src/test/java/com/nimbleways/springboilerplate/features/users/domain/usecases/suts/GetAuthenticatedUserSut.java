package com.nimbleways.springboilerplate.features.users.domain.usecases.suts;

import com.nimbleways.springboilerplate.common.domain.ports.TimeProviderPort;
import com.nimbleways.springboilerplate.common.infra.adapters.fakes.FakeRandomGenerator;
import com.nimbleways.springboilerplate.common.infra.adapters.fakes.FakeTokenClaimsCodec;
import com.nimbleways.springboilerplate.common.infra.adapters.fakes.FakeUserRepository;
import com.nimbleways.springboilerplate.common.infra.adapters.fakes.FakeUserSessionRepository;
import com.nimbleways.springboilerplate.features.authentication.domain.entities.TokenClaims;
import com.nimbleways.springboilerplate.features.authentication.domain.entities.UserPrincipal;
import com.nimbleways.springboilerplate.features.authentication.domain.entities.UserSession;
import com.nimbleways.springboilerplate.features.authentication.domain.properties.TokenProperties;
import com.nimbleways.springboilerplate.features.authentication.domain.valueobjects.UserTokens;
import com.nimbleways.springboilerplate.features.users.domain.entities.User;
import com.nimbleways.springboilerplate.features.users.domain.usecases.getauthenticateduser.GetAuthenticatedUserUseCase;
import com.nimbleways.springboilerplate.testhelpers.configurations.PropertiesTestConfiguration;
import com.nimbleways.springboilerplate.testhelpers.configurations.TimeTestConfiguration;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Import;

import java.time.Instant;

import static com.nimbleways.springboilerplate.testhelpers.fixtures.NewUserFixture.aNewUser;
import static com.nimbleways.springboilerplate.testhelpers.fixtures.SimpleFixture.aUserTokens;
import static com.nimbleways.springboilerplate.testhelpers.helpers.Mapper.toUserPrincipal;

@Getter
@Import({
        GetAuthenticatedUserUseCase.class,
        FakeUserSessionRepository.class,
        FakeUserRepository.class,
        FakeTokenClaimsCodec.class,
        PropertiesTestConfiguration.class,
        TimeTestConfiguration.class,
        FakeRandomGenerator.class
})
@RequiredArgsConstructor
public class GetAuthenticatedUserSut {
    @Getter(AccessLevel.NONE)
    private final GetAuthenticatedUserUseCase useCase;

    private final FakeUserRepository userRepository;
    private final FakeUserSessionRepository userSessionRepository;
    private final TokenProperties tokenProperties;
    private final FakeTokenClaimsCodec tokenGenerator;
    private final TimeProviderPort timeProvider;

    public User handle() {
        return useCase.handle();
    }

    @NotNull
    public TestData addUserAndSessionToRepository() {
        User user = userRepository().create(aNewUser().build());
        TestData testData = createUserSessionAndTokens(user);
        userSessionRepository().create(testData.userSession());
        return testData;
    }

    @NotNull
    private TestData createUserSessionAndTokens(User user) {
        Instant now = timeProvider.instant();
        UserPrincipal userPrincipal = toUserPrincipal(user);
        TokenClaims tokenClaims = new TokenClaims(userPrincipal, now, now.plusSeconds(1));
        UserTokens userTokens = aUserTokens(tokenGenerator.encode(tokenClaims));
        UserSession userSession = new UserSession(
                userTokens.refreshToken(),
                now.plus(tokenProperties.refreshTokenValidityDuration()),
                userPrincipal);
        return new TestData(userSession, userTokens, user);
    }

    public record TestData(UserSession userSession, UserTokens userTokens, User user) {}
}
