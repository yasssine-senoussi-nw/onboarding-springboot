package com.nimbleways.springboilerplate.common.infra.database.entities;

import com.nimbleways.springboilerplate.common.domain.valueobjects.Money;
import com.nimbleways.springboilerplate.common.domain.valueobjects.StarRating;
import com.nimbleways.springboilerplate.common.utils.collections.Immutable;
import com.nimbleways.springboilerplate.features.purchases.domain.entities.Purchase;
import com.nimbleways.springboilerplate.features.purchases.domain.valueobjects.NewPurchase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.Collection;
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
    @NotNull
    private UserDbEntity user;

    @Column(name = "purchase_date")
    @NotNull
    private Instant purchaseDate;

    @NotNull
    private String name;

    @NotNull
    private String model;

    @NotNull
    private String brand;

    @NotNull
    private String store;

    @ElementCollection
    @Column(name = "images")
    @NotNull
    private Collection<String> images;

    private double price;

    private int rating;

    public static PurchaseDbEntity from(NewPurchase purchase, UserDbEntity user) {
        PurchaseDbEntity entity = new PurchaseDbEntity();
        entity.user(user);
        entity.purchaseDate(purchase.purchaseDate());
        entity.brand(purchase.brand());
        entity.price(purchase.price().value());
        entity.rating(purchase.rating().value());
        entity.model(purchase.model());
        entity.store(purchase.store());
        entity.name(purchase.name());
        entity.images(purchase.images().toList());
        return entity;
    }

    public Purchase toPurchase() {
        return new Purchase(
            id,
            user.id(),
            name,
            purchaseDate,
            brand,
            model,
            store,
            Immutable.collectList(this.images, String::new),
            new Money(price),
            new StarRating(rating)
        );
    }
}
