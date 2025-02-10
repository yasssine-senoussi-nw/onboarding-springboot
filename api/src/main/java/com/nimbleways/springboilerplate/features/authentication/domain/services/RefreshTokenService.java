package com.nimbleways.springboilerplate.features.authentication.domain.services;

import com.nimbleways.springboilerplate.common.domain.ports.RandomGeneratorPort;
import com.nimbleways.springboilerplate.common.domain.ports.TimeProviderPort;
import com.nimbleways.springboilerplate.features.authentication.domain.entities.TokenClaims;
import com.nimbleways.springboilerplate.features.authentication.domain.entities.UserPrincipal;
import com.nimbleways.springboilerplate.features.authentication.domain.entities.UserSession;
import com.nimbleways.springboilerplate.features.authentication.domain.exceptions.RefreshAndAccessTokensMismatchException;
import com.nimbleways.springboilerplate.features.authentication.domain.exceptions.RefreshTokenExpiredOrNotFoundException;
import com.nimbleways.springboilerplate.features.authentication.domain.ports.TokenClaimsCodecPort;
import com.nimbleways.springboilerplate.features.authentication.domain.ports.UserSessionRepositoryPort;
import com.nimbleways.springboilerplate.features.authentication.domain.properties.TokenProperties;
import com.nimbleways.springboilerplate.features.authentication.domain.valueobjects.AccessToken;
import com.nimbleways.springboilerplate.features.authentication.domain.valueobjects.RefreshToken;
import com.nimbleways.springboilerplate.features.authentication.domain.valueobjects.UserTokens;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class RefreshTokenService {

    private final UserSessionRepositoryPort userSessionRepository;
    private final TokenProperties tokenProperties;
    private final TokenClaimsCodecPort tokenGenerator;
    private final TimeProviderPort timeProvider;
    private final RandomGeneratorPort randomGenerator;

    public RefreshTokenService(final UserSessionRepositoryPort userSessionRepository,
                               final TokenClaimsCodecPort tokenGenerator,
                               final TokenProperties tokenProperties,
                               final TimeProviderPort timeProvider,
                               final RandomGeneratorPort randomGenerator
    ) {
        this.userSessionRepository = userSessionRepository;
        this.tokenGenerator = tokenGenerator;
        this.tokenProperties = tokenProperties;
        this.timeProvider = timeProvider;
        this.randomGenerator = randomGenerator;
    }

    public UserTokens createUserTokens(final UserPrincipal userPrincipal) {
        return new UserTokens(
            generateNewAccessToken(userPrincipal),
            createRefreshToken(userPrincipal)
        );
    }

    @Transactional
    public UserTokens recreateUserTokens(final UserTokens previousUserTokens) {
        final UserPrincipal userPrincipal = getAssociatedUser(previousUserTokens);
        userSessionRepository.deleteUserSessionByRefreshToken(previousUserTokens.refreshToken());
        return createUserTokens(userPrincipal);
    }

    private RefreshToken createRefreshToken(final UserPrincipal userPrincipal) {
        RefreshToken refreshToken = new RefreshToken(randomGenerator.uuid().toString());
        Instant expirationDate = timeProvider.instant()
            .plus(tokenProperties.refreshTokenValidityDuration());
        UserSession userSession = new UserSession(refreshToken, expirationDate, userPrincipal);
        userSessionRepository.create(userSession);
        return refreshToken;
    }

    private AccessToken generateNewAccessToken(UserPrincipal userPrincipal) {
        Instant now = timeProvider.instant();
        Instant expirationInstant = now.plus(tokenProperties.accessTokenValidityDuration());
        return tokenGenerator.encode(new TokenClaims(userPrincipal, now, expirationInstant));
    }

    private UserPrincipal getAssociatedUser(final UserTokens userTokens) {
        final UserSession userSession = findByRefreshToken(userTokens.refreshToken()).orElseThrow(
            () -> new RefreshTokenExpiredOrNotFoundException(userTokens.refreshToken()));
        final UserPrincipal userPrincipal = userSession.userPrincipal();
        UUID userIdFromAccessToken = tokenGenerator.decodeWithoutExpirationValidation(userTokens.accessToken()).userPrincipal().id();
        UUID userIdFromRefreshToken = userPrincipal.id();
        if (!Objects.equals(userIdFromRefreshToken, userIdFromAccessToken)) {
            throw new RefreshAndAccessTokensMismatchException(userIdFromRefreshToken, userIdFromAccessToken);
        }
        return userPrincipal;
    }

    private Optional<UserSession> findByRefreshToken(final RefreshToken refreshToken) {
        return userSessionRepository.findByRefreshTokenAndExpirationDateAfter(
            refreshToken,
            timeProvider.instant());
    }

}