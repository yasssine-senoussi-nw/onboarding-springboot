package com.nimbleways.springboilerplate.common.api.exceptionhandling;

import lombok.Getter;

@Getter
enum ApiErrorCodes {
    ACCESS_DENIED_ERROR("errors.access_denied"),
    INPUT_FORMAT_ERROR("errors.input_bad_format"),
    INPUT_VALIDATION_ERROR("errors.input_failed_validation"),
    INTERNAL_SERVER_ERROR("errors.internal_server_error"),
    MISSING_BODY_ERROR("errors.missing_body"),
    UNAUTHORIZED_ERROR("errors.unauthorized"),
    EMAIL_ALREADY_EXISTS_ERROR("errors.email_already_exists");

    private final String code;

    ApiErrorCodes(String code) {
        this.code = code;
    }

}
