package com.nimbleways.springboilerplate.testhelpers.configurations;

import com.nimbleways.springboilerplate.common.domain.ports.EmploymentDatePort;
import com.nimbleways.springboilerplate.common.infra.adapters.FakeEmploymentDateProvider;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class EmploymentDateTestConfiguration {

    @Bean
    public static EmploymentDatePort fixedEmploymentDateProvider() {
        return new FakeEmploymentDateProvider(TimeTestConfiguration.fixedTimeProvider());
    }
}
