package com.nimbleways.springboilerplate.testhelpers;

import com.nimbleways.springboilerplate.testhelpers.junitextension.SetupTestDatabaseExtension;
import com.nimbleways.springboilerplate.testhelpers.utils.CookieStoreInterceptor;
import jakarta.annotation.PostConstruct;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.UseMainMethod;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.RequestEntity.BodyBuilder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(
    webEnvironment = WebEnvironment.RANDOM_PORT,
    useMainMethod = UseMainMethod.ALWAYS
)
// Needed so that registerDatasourceProperties is run before each test method
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@Testcontainers
public class BaseE2ETests {
    @Container
    private static final JdbcDatabaseContainer<?> testDatabase =
        SetupTestDatabaseExtension.createJdbcDatabaseContainer();

    @LocalServerPort
    private int port;
    @Value("${server.servlet.context-path}")
    private String contextPath;
    protected TestRestTemplate restTemplate;

    protected BaseE2ETests() {
    }

    @DynamicPropertySource
    private static void registerDatasourceProperties(DynamicPropertyRegistry registry) {
        // I couldn't make the transaction rollback work when using @SpringBootTest
        // with WebEnvironment.RANDOM_PORT. We need rollback to make tests independent and isolated.
        // So the alternative solution is to drop the database and recreate it before each test method
        if (testDatabase.isCreated()) {
            testDatabase.close();
        }
        testDatabase.start();
        registry.add("spring.datasource.url", testDatabase::getJdbcUrl);
        registry.add("spring.datasource.username", testDatabase::getUsername);
        registry.add("spring.datasource.password", testDatabase::getPassword);
    }

    @PostConstruct
    private void post() {
        String baseUrl = String.format("http://localhost:%d%s", port, contextPath);
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
            .interceptors(new CookieStoreInterceptor())
            .rootUri(baseUrl);
        this.restTemplate = new TestRestTemplate(restTemplateBuilder);
    }

    @NotNull
    protected BodyBuilder post(String endpoint) {
        return RequestEntity
            .post(endpoint)
            .contentType(MediaType.APPLICATION_JSON);
    }

}
