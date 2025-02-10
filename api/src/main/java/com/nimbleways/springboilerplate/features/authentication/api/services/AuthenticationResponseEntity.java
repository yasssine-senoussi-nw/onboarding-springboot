package com.nimbleways.springboilerplate.features.authentication.api.services;

import com.nimbleways.springboilerplate.features.authentication.api.endpoints.logout.LogoutEndpoint;
import com.nimbleways.springboilerplate.features.authentication.api.endpoints.recreateusertokens.RecreateUserTokensEndpoint;
import com.nimbleways.springboilerplate.features.authentication.domain.properties.TokenProperties;
import com.nimbleways.springboilerplate.features.authentication.domain.valueobjects.AccessToken;
import com.nimbleways.springboilerplate.features.authentication.domain.valueobjects.RefreshToken;
import com.nimbleways.springboilerplate.features.authentication.domain.valueobjects.UserTokens;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

@Component
public class AuthenticationResponseEntity {
    public static final String ACCESS_TOKEN_COOKIE_NAME = "accessToken";
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";
    private final String apiContextPath;
    private final String refreshTokenEndpointUrl;
    private final String logoutEndpointUrl;
    private final TokenProperties tokenProperties;

    public AuthenticationResponseEntity(
            @Value("${server.servlet.context-path}") final String apiContextPath,
            final TokenProperties tokenProperties
    ) {
        this.apiContextPath = apiContextPath;
        this.tokenProperties = tokenProperties;
        refreshTokenEndpointUrl = apiContextPath + RecreateUserTokensEndpoint.URL;
        logoutEndpointUrl = apiContextPath + LogoutEndpoint.URL;
    }

    public ResponseEntity<Void> from(final UserTokens userTokens) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .headers(createCookiesHttpHeaders(
                createAccessTokenCookie(userTokens.accessToken(), apiContextPath),
                createRefreshTokenCookie(userTokens.refreshToken(), refreshTokenEndpointUrl),
                createRefreshTokenCookie(userTokens.refreshToken(), logoutEndpointUrl)
            ))
            .build();
    }

    public ResponseEntity<Void> expireAuthCookies() {
        return ResponseEntity
            .status(HttpStatus.OK)
            .headers(createCookiesHttpHeaders(
                createExpiredCookie(ACCESS_TOKEN_COOKIE_NAME, apiContextPath),
                createExpiredCookie(REFRESH_TOKEN_COOKIE_NAME, refreshTokenEndpointUrl),
                createExpiredCookie(REFRESH_TOKEN_COOKIE_NAME, logoutEndpointUrl)
            ))
            .build();
    }

    public static @Nullable String retrieveAccessToken(HttpServletRequest request){
        Cookie cookie = WebUtils.getCookie(request, ACCESS_TOKEN_COOKIE_NAME);
        if (cookie != null) {
            return URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8);
        }
        return null;
    }

    private String createAccessTokenCookie(final AccessToken accessToken, final String cookiePath) {
        return createCookie(
            ACCESS_TOKEN_COOKIE_NAME,
            accessToken.value(),
            tokenProperties.refreshTokenValidityDuration(),
            cookiePath
        );
    }

    private String createRefreshTokenCookie(final RefreshToken refreshToken, final String cookiePath) {
        return createCookie(
            REFRESH_TOKEN_COOKIE_NAME,
            refreshToken.value(),
            tokenProperties.refreshTokenValidityDuration(),
            cookiePath
        );
    }

    private String createExpiredCookie(String cookieName, final String cookiePath) {
        return createCookie(cookieName, "deleted", Duration.ZERO, cookiePath);
    }

    @NotNull
    private String createCookie(
        String cookieName,
        String value,
        Duration maxAge,
        String cookiePath
    ) {
        return ResponseCookie
            .from(cookieName, URLEncoder.encode(value, StandardCharsets.UTF_8))
            .httpOnly(true)
            .maxAge(maxAge)
            .secure(tokenProperties.https())
            .path(cookiePath)
            .sameSite("Strict")
            .build()
            .toString();
    }

    private static HttpHeaders createCookiesHttpHeaders(String... cookies) {
        HttpHeaders httpHeaders = new HttpHeaders();
        for (String cookie: cookies) {
            httpHeaders.add(HttpHeaders.SET_COOKIE, cookie);
        }
        return httpHeaders;
    }
}
