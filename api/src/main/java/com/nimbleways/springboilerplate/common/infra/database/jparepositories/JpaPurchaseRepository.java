package com.nimbleways.springboilerplate.common.infra.database.jparepositories;

import com.nimbleways.springboilerplate.common.infra.database.entities.PurchaseDbEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.stream.Stream;

@Repository
public interface JpaPurchaseRepository extends JpaRepository<PurchaseDbEntity, UUID> {
    Stream<PurchaseDbEntity> streamByUser_Id(UUID id);
}
