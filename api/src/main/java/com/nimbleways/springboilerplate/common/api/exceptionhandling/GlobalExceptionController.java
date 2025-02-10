package com.nimbleways.springboilerplate.common.api.exceptionhandling;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.ServletWebRequest;

// This controller handles legacy Spring errors that were not yet migrated to problemDetails
// It extracts the exception from the request and rethrows it to be handled like other exceptions
// See https://github.com/spring-projects/spring-boot/issues/33885
// See https://docs.spring.io/spring-boot/docs/current/reference/html/web.html#web.servlet.spring-mvc.error-handling
@Controller
public class GlobalExceptionController implements ErrorController {
    private static final ErrorAttributes ERROR_ATTRIBUTES_PROVIDER = new DefaultErrorAttributes();

    @RequestMapping({"${server.error.path:${error.path:/error}}"})
    public void handleLegacySpringErrors(HttpServletRequest request) throws Throwable {
        throw ERROR_ATTRIBUTES_PROVIDER.getError(new ServletWebRequest(request));
    }
}
