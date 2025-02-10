package com.nimbleways.springboilerplate.testhelpers.utils;

import com.nimbleways.springboilerplate.common.utils.collections.Mutable;
import java.io.IOException;
import java.net.HttpCookie;
import java.util.List;
import java.util.Map.Entry;
import org.eclipse.collections.api.map.MutableMap;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class CookieStoreInterceptor implements ClientHttpRequestInterceptor {
    private final MutableMap<String, HttpCookie> cookiesStore = Mutable.map.empty();

    @NotNull
    @Override
    public ClientHttpResponse intercept(
        @NotNull HttpRequest request,
        byte @NotNull [] body,
        ClientHttpRequestExecution execution
    ) throws IOException {
        purgeExpiredCookies();
        injectMatchingCookies(request);
        ClientHttpResponse response = execution.execute(request, body);
        storeNewCookies(response);
        return response;
    }

    private void purgeExpiredCookies() {
        cookiesStore.entrySet().stream()
            .filter(c -> c.getValue().hasExpired())
            .map(Entry::getKey)
            .toList()
            .forEach(cookiesStore::remove);
    }

    private void injectMatchingCookies(@NotNull HttpRequest request) {
        cookiesStore.values().stream()
            .filter(c -> request.getURI().getPath().startsWith(c.getPath()))
            .forEach(c -> request.getHeaders().add(HttpHeaders.COOKIE, getCookieString(c)));
    }

    private void storeNewCookies(ClientHttpResponse response) {
        List<String> cookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);
        if (cookies == null) {
            return;
        }
        cookies.stream()
            .flatMap(c -> HttpCookie.parse(c).stream())
            .forEach(c -> cookiesStore.put(c.getName() + ':' + c.getPath(), c));
    }

    private static String getCookieString(HttpCookie cookies) {
        return String.format("%s=%s", cookies.getName(), cookies.getValue());
    }
}
