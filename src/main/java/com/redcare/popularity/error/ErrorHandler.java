package com.redcare.popularity.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String param = ex.getName();
        String value = ex.getValue() != null ? ex.getValue().toString() : "null";
        String expectedType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", "Invalid request parameter");
        body.put("message", String.format("Parameter '%s' has invalid value '%s'. Expected type: %s.", param, value, expectedType));
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("timestamp", Instant.now().toString());

        return ResponseEntity.badRequest().body(body);
    }
}