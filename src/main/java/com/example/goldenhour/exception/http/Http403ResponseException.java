package com.example.goldenhour.exception.http;

import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;

public class Http403ResponseException extends HttpResponseException {

    public Http403ResponseException(String message) {
        super(message);
    }

    @Override
    public List<?> body() {

        return Arrays.asList(getMessage(), HttpStatus.FORBIDDEN);
    }

    @Override
    public HttpStatus status() {

        return HttpStatus.FORBIDDEN;
    }
}
