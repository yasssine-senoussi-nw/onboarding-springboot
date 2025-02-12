package com.nimbleways.springboilerplate.features.authentication.domain.entities;

import com.nimbleways.springboilerplate.common.domain.valueobjects.Role;
import com.nimbleways.springboilerplate.common.domain.valueobjects.Email;
import java.util.UUID;
import org.eclipse.collections.api.set.ImmutableSet;

public record UserPrincipal(
    UUID id,
    Email email,

    ImmutableSet<Role> roles
) {
}
