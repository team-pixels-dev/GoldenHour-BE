package com.example.goldenhour.exception.http;

import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;

public class Http401ResponseException extends HttpResponseException {

    public Http401ResponseException(String message) {

        super(message);
    }

    @Override
    public List<?> body() {

        return Arrays.asList(getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @Override
    public HttpStatus status() {

        return HttpStatus.UNAUTHORIZED;
    }
}
