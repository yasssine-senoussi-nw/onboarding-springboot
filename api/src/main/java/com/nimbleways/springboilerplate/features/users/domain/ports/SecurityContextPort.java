package com.nimbleways.springboilerplate.features.users.domain.ports;

import com.nimbleways.springboilerplate.common.domain.valueobjects.Email;

import java.util.Optional;

public interface SecurityContextPort {
    Optional<Email> getCurrentUserEmail();
}
