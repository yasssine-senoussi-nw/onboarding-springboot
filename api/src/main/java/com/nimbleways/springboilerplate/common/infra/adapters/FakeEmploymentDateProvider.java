package com.nimbleways.springboilerplate.common.infra.adapters;

import com.nimbleways.springboilerplate.common.domain.ports.EmploymentDatePort;
import com.nimbleways.springboilerplate.common.domain.ports.TimeProviderPort;
import com.nimbleways.springboilerplate.common.domain.valueobjects.Email;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class FakeEmploymentDateProvider implements EmploymentDatePort {

    private final TimeProviderPort timeProvider;
    private static final int TIME_LIMIT = 100000;

    public FakeEmploymentDateProvider(final TimeProviderPort timeProvider) {
        this.timeProvider = timeProvider;
    }

    @Override
    public Instant getEmploymentDate(Email email) {
        long offset = generateRandomTimeOffset(email);
        return timeProvider.instant().plusSeconds(offset);
    }

    private static long generateRandomTimeOffset(Email email) {
        return email.hashCode() % TIME_LIMIT;
    }
}
