package com.nimbleways.springboilerplate.features.purchases.domain.ports;

import com.nimbleways.springboilerplate.features.purchases.domain.entities.Purchase;
import com.nimbleways.springboilerplate.features.users.domain.entities.User;
import com.nimbleways.springboilerplate.features.users.domain.ports.UserRepositoryPort;

import static com.nimbleways.springboilerplate.testhelpers.fixtures.NewUserFixture.aNewUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static com.nimbleways.springboilerplate.testhelpers.fixtures.NewPurchaseFixture.aNewPurchase;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        Purchase entity = purchaseRepositoryPort.create(aNewPurchase().build(user.id()));

        // WHEN
        List<Purchase> purchases = purchaseRepositoryPort.findByUserId(user.id()).toList();

        // THEN
        assertEquals(List.of(entity), purchases);
    }

    @Test
    @Transactional
    void findCoworkersPurchases_inexistent_userid_yields_empty() {
        // GIVEN
        UUID userId = UUID.randomUUID();

        // WHEN
        List<Purchase> purchases = purchaseRepositoryPort.findCoworkersPurchases(userId).toList();

        // THEN
        assertEquals(0, purchases.size());
    }

    @Test
    @Transactional
    void findCoworkersPurchases_no_other_user_yields_empty() {
        // GIVEN
        User user = userRepositoryPort.create(aNewUser().build());

        // WHEN
        List<Purchase> purchases = purchaseRepositoryPort.findCoworkersPurchases(user.id()).toList();

        // THEN
        assertEquals(0, purchases.size());
    }

    @Test
    @Transactional
    void findCoworkersPurchases_no_purchase_yields_empty() {
        // GIVEN
        User user1 = userRepositoryPort.create(aNewUser().build());
        User user2 = userRepositoryPort.create(aNewUser().build());

        // WHEN
        List<Purchase> purchases1 = purchaseRepositoryPort.findCoworkersPurchases(user1.id()).toList();
        List<Purchase> purchases2 = purchaseRepositoryPort.findCoworkersPurchases(user2.id()).toList();

        // THEN
        assertEquals(0, purchases1.size());
        assertEquals(0, purchases2.size());
    }

    @Test
    @Transactional
    void findCoworkersPurchases_with_purchases_yields_correct_result() {
        // GIVEN
        User user1 = userRepositoryPort.create(aNewUser().build());
        User user2 = userRepositoryPort.create(aNewUser().build());

        Purchase entity1 = purchaseRepositoryPort.create(aNewPurchase().build(user1.id()));
        Purchase entity2 = purchaseRepositoryPort.create(aNewPurchase().build(user2.id()));

        // WHEN
        List<Purchase> purchases1 = purchaseRepositoryPort.findCoworkersPurchases(user1.id()).toList();
        List<Purchase> purchases2 = purchaseRepositoryPort.findCoworkersPurchases(user2.id()).toList();

        // THEN
        assertEquals(List.of(entity2), purchases1);
        assertEquals(List.of(entity1), purchases2);
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
        Purchase entity = purchaseRepositoryPort.create(aNewPurchase().build(user.id()));

        // WHEN
        Optional<Purchase> purchase = purchaseRepositoryPort.findById(entity.id());

        // THEN
        assertEquals(Optional.of(entity), purchase);
    }

    protected abstract PurchaseRepositoryPort getPurchaseRepository();

    protected abstract UserRepositoryPort getUserRepository();
}
