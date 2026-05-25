package com.azhar.sudoku.exception;

import com.azhar.sudoku.api.SolveResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<SolveResponse> handleUnexpected(Exception ex) {
        return ResponseEntity.internalServerError()
                .body(SolveResponse.invalid("Unexpected error: " + ex.getMessage()));
    }
}
