package com.nimbleways.springboilerplate.common.infra.database.jparepositories;

import com.nimbleways.springboilerplate.common.infra.database.entities.PurchaseDbEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.stream.Stream;

@Repository
public interface JpaPurchaseRepository extends JpaRepository<PurchaseDbEntity, UUID> {
	Stream<PurchaseDbEntity> streamByUser_Id(UUID id);

	@Query("SELECT p FROM PurchaseDbEntity p WHERE p.user.id != :userId")
	Stream<PurchaseDbEntity> findOthersPurchases(UUID userId);
}
