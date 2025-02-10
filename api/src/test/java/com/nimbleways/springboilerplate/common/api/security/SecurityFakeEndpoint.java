package com.nimbleways.springboilerplate.common.api.security;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class SecurityFakeEndpoint {
    public static final String PERMIT_ALL_POST_ENDPOINT = "/security/post";

    @PostMapping(PERMIT_ALL_POST_ENDPOINT)
    @PreAuthorize("permitAll()")
    public void postEndpoint() {
    }
}
