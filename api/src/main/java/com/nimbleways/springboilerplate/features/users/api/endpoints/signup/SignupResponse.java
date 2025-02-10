package com.nimbleways.springboilerplate.features.users.api.endpoints.signup;

import com.nimbleways.springboilerplate.common.infra.mappers.RoleMapper;
import com.nimbleways.springboilerplate.features.users.domain.entities.User;
import org.eclipse.collections.api.set.ImmutableSet;

public record SignupResponse(
    String id,
    String name,
    String username,
    ImmutableSet<String> roles
) {
    public static SignupResponse from(User user) {
        ImmutableSet<String> userRoles = RoleMapper.INSTANCE.fromValueObjects(user.roles());
        return new SignupResponse(
                user.id().toString(),
                user.name(),
                user.username().value(),
                userRoles
        );
    }
}