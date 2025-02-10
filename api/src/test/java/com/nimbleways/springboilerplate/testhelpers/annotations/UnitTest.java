package com.nimbleways.springboilerplate.testhelpers.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Timeout;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Timeout(value = 1000, unit = TimeUnit.MILLISECONDS)
public @interface UnitTest {
}
