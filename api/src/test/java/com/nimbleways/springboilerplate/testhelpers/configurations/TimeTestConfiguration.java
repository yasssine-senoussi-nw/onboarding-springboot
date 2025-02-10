package com.nimbleways.springboilerplate.testhelpers.configurations;

import com.nimbleways.springboilerplate.common.domain.ports.TimeProviderPort;
import com.nimbleways.springboilerplate.common.infra.adapters.TimeProvider;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TimeTestConfiguration {

    @Bean
    public static Clock fixedClock() {
        Instant fixedInstant = Instant.parse("2022-01-01T00:00:00Z");
        return Clock.fixed(fixedInstant, ZoneId.of("UTC"));
    }
    @Bean
    public static TimeProviderPort fixedTimeProvider() {
        return new TimeProvider(fixedClock());
    }
}
