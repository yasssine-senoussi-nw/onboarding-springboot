package com.nimbleways.springboilerplate.features.users.domain.usecases.suts;

import com.nimbleways.springboilerplate.common.infra.adapters.fakes.FakeUserRepository;
import com.nimbleways.springboilerplate.features.users.domain.entities.User;
import com.nimbleways.springboilerplate.features.users.domain.usecases.getusers.GetUsersUseCase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.eclipse.collections.api.list.ImmutableList;
import org.springframework.context.annotation.Import;

@Getter
@Import({
        GetUsersUseCase.class,
        FakeUserRepository.class
})
@RequiredArgsConstructor
public class GetUsersSut {
    @Getter(AccessLevel.NONE)
    private final GetUsersUseCase useCase;

    private final FakeUserRepository userRepository;

    public ImmutableList<User> handle() {
        return useCase.handle();
    }
}
