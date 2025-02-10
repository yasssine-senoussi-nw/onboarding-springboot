package com.nimbleways.springboilerplate.common.api.beans;

import com.fasterxml.jackson.datatype.eclipsecollections.EclipseCollectionsModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class ObjectMapperConfiguration {
    @Bean
    public Jackson2ObjectMapperBuilder jacksonBuilder() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.modulesToInstall(EclipseCollectionsModule.class);
        return builder;
    }
}
