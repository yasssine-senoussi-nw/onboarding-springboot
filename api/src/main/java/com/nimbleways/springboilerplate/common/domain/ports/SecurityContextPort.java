package com.nimbleways.springboilerplate.common.domain.ports;

import com.nimbleways.springboilerplate.common.domain.valueobjects.Email;
import com.nimbleways.springboilerplate.common.domain.valueobjects.UserId;

import java.util.Optional;

public interface SecurityContextPort {
    Optional<UserId> getCurrentUserId();
    Optional<Email> getCurrentUserEmail();
}
