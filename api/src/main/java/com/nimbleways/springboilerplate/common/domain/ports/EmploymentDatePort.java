package com.nimbleways.springboilerplate.common.domain.ports;

import com.nimbleways.springboilerplate.common.domain.valueobjects.Email;

import java.time.Instant;

public interface EmploymentDatePort {
    Instant getEmploymentDate(Email email);
}
