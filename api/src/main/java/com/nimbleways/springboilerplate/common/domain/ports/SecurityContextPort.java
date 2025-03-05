package com.nimbleways.springboilerplate.common.domain.ports;

import com.nimbleways.springboilerplate.common.domain.valueobjects.Email;

import java.util.Optional;
import java.util.UUID;

public interface SecurityContextPort {
    Optional<UUID> getCurrentUserId();
    Optional<Email> getCurrentUserEmail();
}
