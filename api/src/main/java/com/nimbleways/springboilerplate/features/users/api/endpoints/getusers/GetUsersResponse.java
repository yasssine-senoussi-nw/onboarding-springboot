package com.nimbleways.springboilerplate.features.users.api.endpoints.getusers;

import com.nimbleways.springboilerplate.features.users.api.endpoints.getusers.GetUsersResponse.Item;
import com.nimbleways.springboilerplate.features.users.domain.entities.User;
import java.util.ArrayList;
import org.eclipse.collections.api.list.ImmutableList;

public final class GetUsersResponse extends ArrayList<Item> {
    public record Item(
        String id,
        String name,
        String username
    ) {}

    public static GetUsersResponse from(ImmutableList<User> users) {
        GetUsersResponse getUsersResponse = new GetUsersResponse(users.size());
        for (User user: users) {
            getUsersResponse.add(from(user));
        }
        return getUsersResponse;
    }

    private GetUsersResponse(int initialCapacity) {
        super(initialCapacity);
    }

    private static Item from(User user) {
        return new Item(
            user.id().toString(),
            user.name(),
            user.username().value()
        );
    }
}
