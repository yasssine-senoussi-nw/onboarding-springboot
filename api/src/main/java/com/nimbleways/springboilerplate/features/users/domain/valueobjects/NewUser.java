package com.nimbleways.springboilerplate.features.users.domain.valueobjects;

import com.nimbleways.springboilerplate.common.domain.valueobjects.Email;
import com.nimbleways.springboilerplate.common.domain.valueobjects.EncodedPassword;
import com.nimbleways.springboilerplate.common.domain.valueobjects.Role;

import java.time.Instant;

public record NewUser(
        String name,
        Email email,
        EncodedPassword encodedPassword,
        Instant creationDateTime,
        Instant employmentDate,
        Role role
){
}