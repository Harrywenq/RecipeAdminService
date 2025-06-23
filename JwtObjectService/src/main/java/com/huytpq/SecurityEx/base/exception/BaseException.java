package com.huytpq.SecurityEx.base.exception;

import org.springframework.http.HttpStatus;

public class BaseException extends RuntimeException{
    private final Object[] messageArgs;

    private final AbstractError abstractError;

    private final Throwable cause;

    public BaseException(AbstractError abstractError) {
        this(null, abstractError, null);
    }

    public BaseException(Object[] messageArgs, AbstractError abstractError, Throwable cause) {
        this.messageArgs = messageArgs;
        this.abstractError = abstractError;
        this.cause = cause;
    }

    public BaseException(String message, Object[] messageArgs, AbstractError abstractError, Throwable cause) {
        super(message);
        this.messageArgs = messageArgs;
        this.abstractError = abstractError;
        this.cause = cause;
    }

    public BaseException(String message, Throwable cause, Object[] messageArgs, AbstractError abstractError, Throwable cause1) {
        super(message, cause);
        this.messageArgs = messageArgs;
        this.abstractError = abstractError;
        this.cause = cause1;
    }

    public BaseException(Throwable cause, Object[] messageArgs, AbstractError abstractError, Throwable cause1) {
        super(cause);
        this.messageArgs = messageArgs;
        this.abstractError = abstractError;
        this.cause = cause1;
    }

    public BaseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Object[] messageArgs, AbstractError abstractError, Throwable cause1) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.messageArgs = messageArgs;
        this.abstractError = abstractError;
        this.cause = cause1;
    }

    public int getCode() {
        return abstractError.getCode();
    }

    public String getMessage() {
        return messageArgs == null
                ? super.getMessage()
                : String.format(abstractError.getMessage(), messageArgs);
    }

    public String getLocalizedMessage() {
        return BaseException.class.getSimpleName()
                + "["
                + abstractError.getCode()
                + "-"
                + getMessage()
                + "-"
                + abstractError.getHttpStatus()
                + "]";
    }

    public HttpStatus getHttpStatus() {
        return abstractError.getHttpStatus();
    }

    public BaseException getErrorCode() {
        return new BaseException(abstractError);
    }
}
