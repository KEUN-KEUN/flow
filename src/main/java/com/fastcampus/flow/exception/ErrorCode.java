package com.fastcampus.flow.exception;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum ErrorCode {

    QUEUE_ALREADY_REGISTERD_USER(HttpStatus.CONFLICT, "uq-0001", "Already registered user");

    private final HttpStatus httpStatus;
    private final String code;
    private final String reason;

    public ApplicationException build() {
        return new ApplicationException(httpStatus, code, reason);
    }

    public ApplicationException build(Object... args) {
        return new ApplicationException(httpStatus, code, reason.formatted(args));
    }
}
