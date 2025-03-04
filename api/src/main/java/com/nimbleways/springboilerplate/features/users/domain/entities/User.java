package com.nimbleways.springboilerplate.features.users.domain.entities;

import com.nimbleways.springboilerplate.common.domain.valueobjects.Email;
import com.nimbleways.springboilerplate.common.domain.valueobjects.Money;
import com.nimbleways.springboilerplate.common.domain.valueobjects.Role;

import java.time.Instant;
import java.util.UUID;

public record User(
        UUID id,
        String name,
        Email email,
        Instant createdAt,
        Instant employmentDate,
        Role role,
        Money balance
){
}
