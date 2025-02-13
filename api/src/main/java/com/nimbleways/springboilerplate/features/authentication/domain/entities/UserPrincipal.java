package com.nimbleways.springboilerplate.features.authentication.domain.entities;

import com.nimbleways.springboilerplate.common.domain.valueobjects.Role;
import com.nimbleways.springboilerplate.common.domain.valueobjects.Email;
import java.util.UUID;

public record UserPrincipal(
    UUID id,
    Email email,

    Role role
) {
}
