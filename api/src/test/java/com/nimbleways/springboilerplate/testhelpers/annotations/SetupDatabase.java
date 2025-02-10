package com.nimbleways.springboilerplate.testhelpers.annotations;


import com.nimbleways.springboilerplate.testhelpers.junitextension.SetupTestDatabaseExtension;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(SetupTestDatabaseExtension.class)
@ContextConfiguration(initializers = SetupTestDatabaseExtension.Initializer.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public @interface SetupDatabase {}
