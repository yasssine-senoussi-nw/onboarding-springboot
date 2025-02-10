package com.nimbleways.springboilerplate.common.api.beans;

import static com.nimbleways.springboilerplate.Application.BASE_PACKAGE_NAME;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(
    basePackages = BASE_PACKAGE_NAME,
    includeFilters = {
        @ComponentScan.Filter(
            type = FilterType.ASPECTJ,
            pattern = BASE_PACKAGE_NAME + "..domain.usecases..*UseCase"
        )
    },
    useDefaultFilters = false
)
public class DomainBeanConfiguration {
}
