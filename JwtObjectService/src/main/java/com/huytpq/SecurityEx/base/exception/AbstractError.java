package com.huytpq.SecurityEx.base.exception;

import org.springframework.http.HttpStatus;

public interface AbstractError {
    String getMessage();

    int getCode();

    HttpStatus getHttpStatus();
}
