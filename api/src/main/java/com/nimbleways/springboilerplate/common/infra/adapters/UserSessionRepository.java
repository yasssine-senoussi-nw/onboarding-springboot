package com.nimbleways.springboilerplate.common.infra.adapters;

import com.nimbleways.springboilerplate.common.infra.database.entities.UserDbEntity;
import com.nimbleways.springboilerplate.common.infra.database.entities.UserSessionDbEntity;
import com.nimbleways.springboilerplate.common.infra.database.jparepositories.JpaUserRepository;
import com.nimbleways.springboilerplate.common.infra.database.jparepositories.JpaUserSessionRepository;
import com.nimbleways.springboilerplate.features.authentication.domain.entities.UserSession;
import com.nimbleways.springboilerplate.features.authentication.domain.exceptions.CannotCreateUserSessionInRepositoryException;
import com.nimbleways.springboilerplate.features.authentication.domain.exceptions.RefreshTokenExpiredOrNotFoundException;
import com.nimbleways.springboilerplate.features.authentication.domain.ports.UserSessionRepositoryPort;
import com.nimbleways.springboilerplate.features.authentication.domain.valueobjects.RefreshToken;
import java.time.Instant;
import java.util.Optional;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

@Component
public class UserSessionRepository implements UserSessionRepositoryPort {
    private final JpaUserSessionRepository jpaUserSessionRepository;
    private final JpaUserRepository jpaUserRepository;

    public UserSessionRepository(
        JpaUserSessionRepository jpaUserSessionRepository,
        JpaUserRepository jpaUserRepository
    ) {
        this.jpaUserSessionRepository = jpaUserSessionRepository;
        this.jpaUserRepository = jpaUserRepository;
    }

    @Override
    public void create(UserSession userSession) {
        UserDbEntity userDbEntity = jpaUserRepository.getReferenceById(userSession.userPrincipal().id());
        UserSessionDbEntity userSessionDbEntity = UserSessionDbEntity.from(userSession, userDbEntity);
        try {
            jpaUserSessionRepository.saveAndFlush(userSessionDbEntity);
        } catch (DataAccessException ex) {
            throw new CannotCreateUserSessionInRepositoryException(userSession.userPrincipal().email(), ex);
        }
    }

    @Override
    public Optional<UserSession> findByRefreshTokenAndExpirationDateAfter(
        RefreshToken refreshToken,
        Instant now
    ) {
        return jpaUserSessionRepository
            .findValidSessionByRefreshToken(refreshToken.value(), now)
            .map(UserSessionDbEntity::toUserSession);
    }

    @Override
    public void deleteUserSessionByRefreshToken(RefreshToken refreshToken) {
        int count = jpaUserSessionRepository.deleteUserSessionByRefreshToken(refreshToken.value());
        if (count == 0) {
            throw new RefreshTokenExpiredOrNotFoundException(refreshToken);
        }
    }

    @Override
    public void deleteUserSessionByExpirationDateBefore(Instant instant) {
        jpaUserSessionRepository.deleteUserSessionByExpirationDateBefore(instant);
    }
}
