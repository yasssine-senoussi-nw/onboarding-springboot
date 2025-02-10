package com.nimbleways.springboilerplate.common.infra.adapters.fakes;

import com.nimbleways.springboilerplate.features.authentication.domain.entities.UserSession;
import com.nimbleways.springboilerplate.features.authentication.domain.ports.UserSessionRepositoryPort;
import com.nimbleways.springboilerplate.features.authentication.domain.ports.UserSessionRepositoryPortContractTests;
import com.nimbleways.springboilerplate.features.users.domain.ports.UserRepositoryPort;
import com.nimbleways.springboilerplate.testhelpers.annotations.UnitTest;
import org.eclipse.collections.api.list.ImmutableList;

@UnitTest
public class FakeUserSessionRepositoryUnitTests extends UserSessionRepositoryPortContractTests {

    private final FakeUserRepository fakeUserRepository;
    private final FakeUserSessionRepository fakeUserSessionRepository;

    public FakeUserSessionRepositoryUnitTests() {
        super();
        fakeUserRepository = new FakeUserRepository();
        fakeUserSessionRepository = new FakeUserSessionRepository(fakeUserRepository);
    }

    @Override
    protected UserSessionRepositoryPort getUserSessionRepository() {
        return fakeUserSessionRepository;
    }

    @Override
    protected UserRepositoryPort getUserRepository() {
        return fakeUserRepository;
    }

    @Override
    protected ImmutableList<UserSession> getAllUserSessions() {
        return fakeUserSessionRepository.findAll();
    }
}
