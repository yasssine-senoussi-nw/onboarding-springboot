package com.nimbleways.springboilerplate.common.infra.adapters;

import com.nimbleways.springboilerplate.common.infra.database.entities.UserSessionDbEntity;
import com.nimbleways.springboilerplate.common.infra.database.jparepositories.JpaUserRepository;
import com.nimbleways.springboilerplate.common.infra.database.jparepositories.JpaUserSessionRepository;
import com.nimbleways.springboilerplate.common.utils.collections.Immutable;
import com.nimbleways.springboilerplate.features.authentication.domain.entities.UserSession;
import com.nimbleways.springboilerplate.features.authentication.domain.ports.UserSessionRepositoryPort;
import com.nimbleways.springboilerplate.features.authentication.domain.ports.UserSessionRepositoryPortContractTests;
import com.nimbleways.springboilerplate.features.users.domain.ports.UserRepositoryPort;
import com.nimbleways.springboilerplate.testhelpers.annotations.SetupDatabase;
import org.eclipse.collections.api.list.ImmutableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@SetupDatabase
@Import({UserSessionRepository.class, UserRepository.class})
@DataJpaTest
public class UserSessionRepositoryIntegrationTests extends UserSessionRepositoryPortContractTests {

    @Autowired
    private UserSessionRepository userSessionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JpaUserRepository jpaUserRepository;
    @Autowired
    private JpaUserSessionRepository jpaUserSessionRepository;

    @Override
    protected UserSessionRepositoryPort getUserSessionRepository() {
        return userSessionRepository;
    }

    @Override
    protected UserRepositoryPort getUserRepository() {
        return userRepository;
    }

    @Override
    protected ImmutableList<UserSession> getAllUserSessions() {
        return Immutable.collectList(jpaUserSessionRepository.findAll(), UserSessionDbEntity::toUserSession);
    }
}
