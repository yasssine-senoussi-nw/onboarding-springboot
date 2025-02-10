package com.nimbleways.springboilerplate.common.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nimbleways.springboilerplate.common.api.events.UnhandledExceptionEvent;
import com.nimbleways.springboilerplate.common.domain.events.Event;
import com.nimbleways.springboilerplate.common.domain.ports.EventPublisherPort;
import com.nimbleways.springboilerplate.common.infra.adapters.SpringEventPublisher;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.ILoggerFactory;
import org.slf4j.event.Level;
import org.slf4j.event.LoggingEvent;
import org.slf4j.event.SubstituteLoggingEvent;
import org.slf4j.helpers.SubstituteLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Import({
    LoggingEventListener.class, // SUT
    SpringEventPublisher.class})
class LoggingEventListenerIntegrationTests {

    @Autowired
    private EventPublisherPort eventPublisher;
    @Autowired
    private InMemoryLogs inMemoryLogs; // used by ILoggerFactory

    @TestConfiguration
    static class TestConfig {
        @Bean
        public static InMemoryLogs inMemoryLogs() {
            return new InMemoryLogs();
        }

        @Bean
        public static ILoggerFactory loggerFactory(InMemoryLogs inMemoryLogs) {
            return s -> new SubstituteLogger(s,
                    inMemoryLogs.eventQueue, false);
        }
    }

    @BeforeEach
    public void clearLogs() {
        inMemoryLogs.clear();
    }

    @Test
    void exception_event_is_logged_once() {
        // GIVEN
        UnhandledExceptionEvent event = new UnhandledExceptionEvent(new Exception("This is an exception"));

        // WHEN
        eventPublisher.publishEvent(event);

        // THEN
        Optional<LoggingEvent> logEvent = inMemoryLogs.getLoggingEvent("java.lang.Exception: This is an exception");
        assertTrue(logEvent.isPresent());
        assertEquals(LoggingEventListenerIntegrationTests.class.getName(), logEvent.get().getLoggerName());
        assertEquals(event.exception(), logEvent.get().getThrowable());
        assertEquals(Level.ERROR, logEvent.get().getLevel());
        assertEquals(1, inMemoryLogs.size());
    }

    @Test
    void domain_event_is_logged_once() {
        // GIVEN
        FakeEvent event = new FakeEvent();

        // WHEN
        eventPublisher.publishEvent(event);

        // THEN
        Optional<LoggingEvent> logEvent = inMemoryLogs.getLoggingEvent("This is a FakeEvent");
        assertTrue(logEvent.isPresent());
        assertEquals(LoggingEventListenerIntegrationTests.class.getName(), logEvent.get().getLoggerName());
        assertEquals(Level.INFO, logEvent.get().getLevel());
        assertEquals(1, inMemoryLogs.size());
    }

    @Test
    void logger_name_is_UnhandledExceptionEvent_when_unknown_thrower() {
        // GIVEN
        Exception exception = getExceptionWithInvalidStack();
        UnhandledExceptionEvent event = new UnhandledExceptionEvent(exception);

        // WHEN
        eventPublisher.publishEvent(event);

        // THEN
        Optional<LoggingEvent> logEvent = inMemoryLogs.getLoggingEvent(exception.toString());
        assertTrue(logEvent.isPresent());
        assertEquals(UnhandledExceptionEvent.class.getName(), logEvent.get().getLoggerName());
        assertEquals(event.exception(), logEvent.get().getThrowable());
        assertEquals(Level.ERROR, logEvent.get().getLevel());
        assertEquals(1, inMemoryLogs.size());
    }

    private static Exception getExceptionWithInvalidStack() {
        Exception exception = new Exception("This is an exception");
        StackTraceElement invalidStackTrace = new StackTraceElement("InvalidClassName", "methodName", "fileName", 1);
        exception.setStackTrace(new StackTraceElement[]{ invalidStackTrace });
        return exception;
    }

    static class InMemoryLogs {
        private final ConcurrentLinkedQueue<SubstituteLoggingEvent> eventQueue;

        public InMemoryLogs() {
            this.eventQueue = new ConcurrentLinkedQueue<>();
        }

        public int size() {
            return eventQueue.size();
        }

        public Optional<LoggingEvent> getLoggingEvent(String message) {
            return eventQueue.stream().filter(log -> log.getMessage().equals(message)).findFirst().map(e -> e);
        }

        public void clear() {
            eventQueue.clear();
        }
    }

    private record FakeEvent() implements Event {

        @Override
        public String toString() {
            return "This is a FakeEvent";
        }

        @Override
        public Class<?> sourceType() {
            return LoggingEventListenerIntegrationTests.class;
        }
    }
}
