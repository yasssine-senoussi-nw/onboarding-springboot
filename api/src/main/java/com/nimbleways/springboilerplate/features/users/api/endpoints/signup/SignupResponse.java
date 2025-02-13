package com.nimbleways.springboilerplate.features.users.api.endpoints.signup;

import com.nimbleways.springboilerplate.common.infra.mappers.RoleMapper;
import com.nimbleways.springboilerplate.features.users.domain.entities.User;

public record SignupResponse(
    String id,
    String name,
    String email,
    String role
) {
    public static SignupResponse from(User user) {
        String userRole = RoleMapper.INSTANCE.fromValueObject(user.role());
        return new SignupResponse(
                user.id().toString(),
                user.name(),
                user.email().value(),
                userRole
        );
    }
}