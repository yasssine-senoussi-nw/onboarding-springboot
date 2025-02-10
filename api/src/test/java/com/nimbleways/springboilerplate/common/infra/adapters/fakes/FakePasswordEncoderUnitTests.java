package com.nimbleways.springboilerplate.common.infra.adapters.fakes;

import com.nimbleways.springboilerplate.common.domain.ports.PasswordEncoderPort;
import com.nimbleways.springboilerplate.common.domain.ports.PasswordEncoderPortContractTests;
import com.nimbleways.springboilerplate.testhelpers.annotations.UnitTest;

@UnitTest
public class FakePasswordEncoderUnitTests extends PasswordEncoderPortContractTests {

    private static final PasswordEncoderPort INSTANCE = new FakePasswordEncoder();

    @Override
    protected PasswordEncoderPort getInstance() {
        return INSTANCE;
    }
}
