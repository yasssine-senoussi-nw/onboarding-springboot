package com.nimbleways.springboilerplate.common.infra.database.entities;

import com.nimbleways.springboilerplate.features.purchases.domain.entities.Purchase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "purchases")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseDbEntity {
    @Id
    @Column(name = "id")
    @UuidGenerator
    @NotNull
    private UUID id;

    @ManyToOne
    private UserDbEntity user;

    @Column(name = "purchase_date")
    @NotNull
    private Instant purchaseDate;

    public Purchase toPurchase() {
        return new Purchase(
                id,
                user.id(),
                purchaseDate
        );
    }
}
