package com.example.goldenhour.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class Http400ResponseException extends HttpResponseException {

    public Http400ResponseException(String message) {

        super(message);
    }

    public List<?> body() {

        return Arrays.asList(getMessage(), HttpStatus.BAD_REQUEST);
    }

    public HttpStatus status() {

        return HttpStatus.BAD_REQUEST;
    }
}
