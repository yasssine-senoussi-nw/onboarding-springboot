package com.nimbleways.springboilerplate.common.infra.adapters.fakes;

import com.nimbleways.springboilerplate.common.domain.ports.RandomGeneratorPort;
import com.nimbleways.springboilerplate.common.domain.ports.RandomGeneratorPortContractTests;
import com.nimbleways.springboilerplate.testhelpers.annotations.UnitTest;

@UnitTest
public class FakeRandomGeneratorUnitTests extends RandomGeneratorPortContractTests {

    @Override
    protected RandomGeneratorPort getInstance() {
        return new FakeRandomGenerator();
    }
}
