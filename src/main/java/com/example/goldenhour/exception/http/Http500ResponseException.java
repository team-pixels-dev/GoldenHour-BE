package com.example.goldenhour.exception.http;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;

@Getter
public class Http500ResponseException extends HttpResponseException {

    public Http500ResponseException(String message) {
        super(message);
    }

    @Override
    public List<?> body() {

        return Arrays.asList(getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public HttpStatus status() {

        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
