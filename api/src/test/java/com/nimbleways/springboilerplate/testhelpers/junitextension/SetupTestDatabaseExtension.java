package com.nimbleways.springboilerplate.testhelpers.junitextension;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

public class SetupTestDatabaseExtension extends AbstractBeforeAllTestsExtension {
    private static final JdbcDatabaseContainer<?> testDatabase = createJdbcDatabaseContainer();

    @NotNull
    public static JdbcDatabaseContainer<?> createJdbcDatabaseContainer() {
        return new PostgreSQLContainer<>(getPostgresImageNameFromDockerCompose());
    }

    @SneakyThrows
    private static DockerImageName getPostgresImageNameFromDockerCompose() {
        File dockerComposeFile = new File("../docker-compose.dependencies.yml");
        try (FileInputStream fis = new FileInputStream(dockerComposeFile)) {
            Map<?, ?> yamlMap = new Yaml().loadAs(fis, Map.class);
            Map<?, ?> services = getMap(yamlMap, "services");
            Map<?, ?>  postgres = getMap(services, "db");
            if (!(postgres.get("image") instanceof String image)) {
                throw new YAMLException("Invalid YAML structure: 'image' is not a String");
            }
            // remove tag when digest is used because testcontainers does not support both
            String fullImageName = image.replaceAll(":.+?@", "@");
            return DockerImageName.parse(fullImageName);
        }
    }

    private static Map<?, ?> getMap(Map<?, ?> yamlMap, String propertyName) {
        Object propertyObject = yamlMap.get(propertyName);
        if (propertyObject == null) {
            throw new YAMLException("Invalid YAML structure: '" + propertyName + "' is null");
        }
        if (!(propertyObject instanceof Map<?, ?> typedProperty)) {
            throw new YAMLException("Invalid YAML structure: '" + propertyName + "' is not a Map");
        }
        return typedProperty;
    }

    @Override
    void setup() {
        testDatabase.start();
    }

    @Override
    public void close() {
        testDatabase.stop();
    }

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + testDatabase.getJdbcUrl(),
                    "spring.datasource.username=" + testDatabase.getUsername(),
                    "spring.datasource.password=" + testDatabase.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}
