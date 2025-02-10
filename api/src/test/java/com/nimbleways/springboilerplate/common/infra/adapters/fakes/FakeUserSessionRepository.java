package com.nimbleways.springboilerplate.common.infra.adapters.fakes;

import com.nimbleways.springboilerplate.common.utils.collections.Mutable;
import com.nimbleways.springboilerplate.features.authentication.domain.entities.UserSession;
import com.nimbleways.springboilerplate.features.authentication.domain.exceptions.CannotCreateUserSessionInRepositoryException;
import com.nimbleways.springboilerplate.features.authentication.domain.exceptions.RefreshTokenExpiredOrNotFoundException;
import com.nimbleways.springboilerplate.features.authentication.domain.ports.UserSessionRepositoryPort;
import com.nimbleways.springboilerplate.features.authentication.domain.valueobjects.RefreshToken;
import java.time.Instant;
import java.util.Optional;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.springframework.dao.DataIntegrityViolationException;

public class FakeUserSessionRepository implements UserSessionRepositoryPort {

    private final MutableMap<RefreshToken, UserSession> sessionTable = Mutable.map.empty();
    private final FakeUserRepository userRepository;

    public FakeUserSessionRepository(FakeUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void create(UserSession userSession) {
        if (sessionTable.containsKey(userSession.refreshToken())) {
            throw new CannotCreateUserSessionInRepositoryException(
                userSession.userPrincipal().email(),
                new DataIntegrityViolationException(
                    String.format("RefreshToken '%s' already exist in repository",
                        userSession.refreshToken().value())
                ));
        }
        if (!userRepository.userIdExists(userSession.userPrincipal().id())) {
            throw new CannotCreateUserSessionInRepositoryException(
                userSession.userPrincipal().email(),
                new DataIntegrityViolationException(
                    String.format("User ID '%s' not found in repository",
                        userSession.userPrincipal().id())
                ));
        }
        sessionTable.put(userSession.refreshToken(), userSession);
    }

    @Override
    public Optional<UserSession> findByRefreshTokenAndExpirationDateAfter(
        RefreshToken refreshToken,
        Instant now) {
        return Optional
            .ofNullable(sessionTable.get(refreshToken))
            .filter(us -> us.expirationDate().isAfter(now) || us.expirationDate().equals(now));
    }

    @Override
    public void deleteUserSessionByRefreshToken(RefreshToken refreshToken) {
        UserSession removedSession = sessionTable.remove(refreshToken);
        if (removedSession == null) {
            throw new RefreshTokenExpiredOrNotFoundException(refreshToken);
        }
    }

    @Override
    public void deleteUserSessionByExpirationDateBefore(Instant instant) {
        sessionTable
            .values()
            .stream()
            .filter(us -> us.expirationDate().isBefore(instant))
            .map(UserSession::refreshToken)
            .toList() // necessary to avoid ConcurrentModificationException
            .forEach(sessionTable::remove);
    }

    public boolean exists(RefreshToken refreshToken) {
        return sessionTable.containsKey(refreshToken);
    }

    public ImmutableList<UserSession> findAll() {
        return sessionTable.toImmutableList();
    }
}
