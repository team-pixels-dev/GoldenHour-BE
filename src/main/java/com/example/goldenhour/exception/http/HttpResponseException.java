package com.example.goldenhour.exception.http;

import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;

public abstract class HttpResponseException extends RuntimeException {

    public HttpResponseException(String message) {

        super(message);
    }

    abstract public List<?> body();
    abstract public HttpStatus status();
}
