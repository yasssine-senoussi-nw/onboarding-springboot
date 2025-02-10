package com.nimbleways.springboilerplate.features.users.api.endpoints.signup;

import com.nimbleways.springboilerplate.common.domain.valueobjects.Role;
import com.nimbleways.springboilerplate.common.domain.valueobjects.Email;
import com.nimbleways.springboilerplate.common.api.annotations.Parsable;
import com.nimbleways.springboilerplate.common.infra.mappers.RoleMapper;
import com.nimbleways.springboilerplate.features.users.domain.usecases.signup.SignupCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.set.ImmutableSet;

public record SignupRequest (
    @NotBlank
    String name,
    @NotBlank
    String email,
    @NotBlank
    String password,
    @NotNull
    ImmutableList<@Parsable(RoleMapper.class) String> roles
) {
    public SignupCommand toSignupCommand() {
        ImmutableSet<Role> rolesAsEnum = RoleMapper.INSTANCE.toValueObjects(roles());
        return new SignupCommand(name(), new Email(email()), password(), rolesAsEnum);
    }
}