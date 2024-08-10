package com.example.goldenhour.exception;

import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;

public abstract class HttpResponseException extends RuntimeException {

    Http400ResponseException

    abstract public List<?> body();
    abstract public HttpStatus status();
}
