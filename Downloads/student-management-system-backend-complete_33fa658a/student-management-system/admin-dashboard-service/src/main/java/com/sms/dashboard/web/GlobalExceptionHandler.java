package com.sms.dashboard.web;

import com.sms.dashboard.api.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> validation(MethodArgumentNotValidException e) {
        var errs = e.getBindingResult().getFieldErrors().stream()
            .map(f -> new ApiResponse.ApiError(f.getField(), f.getDefaultMessage())).toList();
        return ResponseEntity.badRequest().body(ApiResponse.error("Validation failed", errs));
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> illegal(IllegalArgumentException e) {
        return ResponseEntity.status(404).body(ApiResponse.error(e.getMessage(), List.of()));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> generic(Exception e) {
        return ResponseEntity.status(500).body(ApiResponse.error("Internal error: " + e.getMessage(), List.of()));
    }
}
