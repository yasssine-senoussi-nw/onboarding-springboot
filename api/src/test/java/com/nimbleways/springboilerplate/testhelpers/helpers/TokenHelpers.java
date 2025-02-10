package com.nimbleways.springboilerplate.testhelpers.helpers;

import com.nimbleways.springboilerplate.features.authentication.domain.valueobjects.UserTokens;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public final class TokenHelpers {
    private TokenHelpers() {
    }

    public static String urlEncodeAccessToken(UserTokens userTokens) {
        return urlEncode(userTokens.accessToken().value());
    }

    public static String urlEncodeRefreshToken(UserTokens userTokens) {
        return urlEncode(userTokens.refreshToken().value());
    }

    public static String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
