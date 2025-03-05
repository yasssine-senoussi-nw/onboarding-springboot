package com.nimbleways.springboilerplate.features.purchases.domain.ports;

import com.nimbleways.springboilerplate.features.purchases.domain.entities.Purchase;
import com.nimbleways.springboilerplate.features.purchases.domain.valueobjects.NewPurchase;
import com.nimbleways.springboilerplate.features.users.domain.entities.User;
import com.nimbleways.springboilerplate.features.users.domain.ports.UserRepositoryPort;

import static com.nimbleways.springboilerplate.testhelpers.fixtures.NewUserFixture.aNewUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public abstract class PurchaseRepositoryPortContractTests {
    private PurchaseRepositoryPort purchaseRepositoryPort;
    private UserRepositoryPort userRepositoryPort;

    @BeforeEach
    void createSut() {
        purchaseRepositoryPort = getPurchaseRepository();
        userRepositoryPort = getUserRepository();
    }

    @Test
    @Transactional
    void find_by_inexistent_userid_yields_empty() {
        // GIVEN
        UUID userId = UUID.randomUUID();

        // WHEN
        List<Purchase> purchases = purchaseRepositoryPort.findByUserId(userId).toList();

        // THEN
        assertEquals(0, purchases.size());
    }

    @Test
    @Transactional
    void find_by_userid_with_no_purchases_yields_empty() {
        // GIVEN
        User user = userRepositoryPort.create(aNewUser().build());

        // WHEN
        List<Purchase> purchases = purchaseRepositoryPort.findByUserId(user.id()).toList();

        // THEN
        assertEquals(0, purchases.size());
    }

    @Test
    @Transactional
    void find_by_userid_with_purchases_yields_correct_result() {
        // GIVEN
        User user = userRepositoryPort.create(aNewUser().build());
        NewPurchase purchase = new NewPurchase(user.id(), Instant.now());

        Purchase entity = purchaseRepositoryPort.create(purchase);

        // WHEN
        List<Purchase> purchases = purchaseRepositoryPort.findByUserId(user.id()).toList();

        // THEN
        assertEquals(List.of(entity), purchases);
    }

    @Test
    @Transactional
    void find_by_invalid_id_yields_empty() {
        // GIVEN
        UUID purchaseId = UUID.randomUUID();

        // WHEN
        Optional<Purchase> purchase = purchaseRepositoryPort.findById(purchaseId);

        // THEN
        assertTrue(purchase.isEmpty());
    }

    @Test
    @Transactional
    void find_by_valid_id_yields_correct_result() {
        // GIVEN
        User user = userRepositoryPort.create(aNewUser().build());
        NewPurchase newPurchase = new NewPurchase(user.id(), Instant.now());
        Purchase entity = purchaseRepositoryPort.create(newPurchase);

        // WHEN
        Optional<Purchase> purchase = purchaseRepositoryPort.findById(entity.id());

        // THEN
        assertEquals(Optional.of(entity), purchase);
    }

    protected abstract PurchaseRepositoryPort getPurchaseRepository();

    protected abstract UserRepositoryPort getUserRepository();
}
