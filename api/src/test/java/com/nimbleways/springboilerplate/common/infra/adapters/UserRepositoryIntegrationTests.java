package com.nimbleways.springboilerplate.common.infra.adapters;

import com.nimbleways.springboilerplate.common.infra.database.jparepositories.JpaUserRepository;
import com.nimbleways.springboilerplate.features.authentication.domain.ports.UserCredentialsRepositoryPort;
import com.nimbleways.springboilerplate.features.users.domain.ports.UserRepositoryPort;
import com.nimbleways.springboilerplate.features.users.domain.ports.UserRepositoryPortContractTests;
import com.nimbleways.springboilerplate.testhelpers.annotations.SetupDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@SetupDatabase
@Import({UserRepository.class})
@DataJpaTest
public class UserRepositoryIntegrationTests extends UserRepositoryPortContractTests {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JpaUserRepository jpaUserRepository;

    @Override
    protected UserRepositoryPort getUserRepository() {
        return userRepository;
    }

    @Override
    protected UserCredentialsRepositoryPort getUserCredentialsRepository() {
        return userRepository;
    }
}
