package com.nimbleways.springboilerplate.features.authentication.api.schedules.cleanupsessions;

import com.nimbleways.springboilerplate.features.authentication.domain.usecases.purgerefreshtokens.PurgeRefreshTokensUseCase;
import com.nimbleways.springboilerplate.testhelpers.BaseScheduledTaskIntegrationTests;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.Mockito.*;

@Import(CleanUpUserSessionsScheduledTask.class)
class CleanUpUserSessionsScheduledTaskIntegrationTests extends BaseScheduledTaskIntegrationTests {

    @MockitoBean
    private PurgeRefreshTokensUseCase useCase;

    // for this use case, we only need to make sure
    // that the handle method is called.
    @Test
    void useCase_is_called() {
        verify(useCase, times(1)).handle();
    }
}
