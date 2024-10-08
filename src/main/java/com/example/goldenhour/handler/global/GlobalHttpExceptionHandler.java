package com.example.goldenhour.handler.global;

import com.example.goldenhour.exception.http.Http400ResponseException;
import com.example.goldenhour.exception.http.Http403ResponseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalHttpExceptionHandler {

    @ExceptionHandler(Http400ResponseException.class)
    public ResponseEntity<?> badRequest(Http400ResponseException e) {

        return new ResponseEntity<>(e.body(), e.status());
    }

    @ExceptionHandler(Http403ResponseException.class)
    public ResponseEntity<?> forbidden(Http403ResponseException e) {

        return new ResponseEntity<>(e.body(), e.status());
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<?> nullPointerException(NullPointerException e) {

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(e.getMessage());
    }
}
