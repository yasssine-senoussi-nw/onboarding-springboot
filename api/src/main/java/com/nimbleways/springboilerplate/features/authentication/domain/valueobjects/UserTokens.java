package com.nimbleways.springboilerplate.features.authentication.domain.valueobjects;

public record UserTokens(
    AccessToken accessToken,
    RefreshToken refreshToken
) {
}
