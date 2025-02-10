package com.nimbleways.springboilerplate.common.infra.database.entities;

import com.nimbleways.springboilerplate.common.domain.valueobjects.Role;
import com.nimbleways.springboilerplate.common.infra.mappers.RoleMapper;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "roles")
public class RoleDbEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Nullable
    private Long id;

    @Column(name = "role_name")
    @NotNull
    private String roleName;

    public static RoleDbEntity newFromRole(Role role) {
        RoleDbEntity roleDbEntity = new RoleDbEntity();
        roleDbEntity.roleName(RoleMapper.INSTANCE.fromValueObject(role));
        return roleDbEntity;
    }

    public Role toRole() {
        return RoleMapper.INSTANCE.toValueObject(roleName);
    }
}