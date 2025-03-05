package com.nimbleways.springboilerplate.common.infra.adapters.fakes;

import com.nimbleways.springboilerplate.features.purchases.domain.ports.PurchaseRepositoryPort;
import com.nimbleways.springboilerplate.features.purchases.domain.ports.PurchaseRepositoryPortContractTests;
import com.nimbleways.springboilerplate.features.users.domain.ports.UserRepositoryPort;
import com.nimbleways.springboilerplate.testhelpers.annotations.UnitTest;

@UnitTest
class FakePurchaseRepositoryUnitTests extends PurchaseRepositoryPortContractTests {
    private final PurchaseRepositoryPort purchaseRepositoryPort = new FakePurchaseRepository();
    private final UserRepositoryPort userRepositoryPort = new FakeUserRepository();

    @Override
    protected PurchaseRepositoryPort getPurchaseRepository() {
        return purchaseRepositoryPort;
    }

    @Override
    protected UserRepositoryPort getUserRepository() {
        return userRepositoryPort;
    }
}
