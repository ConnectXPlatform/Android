package com.sagiziv.connectx.repositories;

public class StatusCodeException extends RuntimeException {
    private final int statusCode;

    public StatusCodeException(int statusCode) {
        super("Request returned with status code: " + statusCode);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
