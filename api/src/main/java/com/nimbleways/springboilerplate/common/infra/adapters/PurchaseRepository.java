package com.nimbleways.springboilerplate.common.infra.adapters;

import com.nimbleways.springboilerplate.common.infra.database.entities.PurchaseDbEntity;
import com.nimbleways.springboilerplate.common.infra.database.entities.UserDbEntity;
import com.nimbleways.springboilerplate.common.infra.database.jparepositories.JpaPurchaseRepository;
import com.nimbleways.springboilerplate.common.infra.database.jparepositories.JpaUserRepository;
import com.nimbleways.springboilerplate.features.purchases.domain.entities.Purchase;
import com.nimbleways.springboilerplate.features.purchases.domain.ports.PurchaseRepositoryPort;
import com.nimbleways.springboilerplate.features.purchases.domain.valueobjects.NewPurchase;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Component
public class PurchaseRepository implements PurchaseRepositoryPort {
	private final JpaPurchaseRepository purchaseRepository;
	private final JpaUserRepository userRepository;

	public PurchaseRepository(JpaPurchaseRepository purchaseRepository, JpaUserRepository userRepository) {
		this.purchaseRepository = purchaseRepository;
		this.userRepository = userRepository;
	}

	@Override
	@Transactional
	public Purchase create(NewPurchase purchase) {
		UserDbEntity user = userRepository.findById(purchase.userId()).orElseThrow();
		PurchaseDbEntity entity = PurchaseDbEntity.from(purchase, user);

		entity = purchaseRepository.saveAndFlush(entity);
		return entity.toPurchase();
	}

	@Override
	public Optional<Purchase> findById(UUID purchaseId) {
		return purchaseRepository
			.findById(purchaseId)
			.map(PurchaseDbEntity::toPurchase);
	}

	@Override
	public Stream<Purchase> findByUserId(UUID userId) {
		return purchaseRepository
			.streamByUser_Id(userId)
			.map(PurchaseDbEntity::toPurchase);
	}

	@Override
	public Stream<Purchase> findCoworkersPurchases(UUID userId) {
		return purchaseRepository
			.findOthersPurchases(userId)
			.map(PurchaseDbEntity::toPurchase);
	}
}
