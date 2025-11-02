package com.redcare.popularity.error;

import com.redcare.popularity.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String param = ex.getName();
        Object paramValue = ex.getValue();
        String value = paramValue != null ? paramValue.toString() : "null";
        Class<?> requiredType = ex.getRequiredType();
        String expectedType = requiredType != null ? requiredType.getSimpleName() : "unknown";

        ErrorResponse errorResponse = new ErrorResponse(
                "Invalid request parameter",
                String.format("Parameter '%s' has invalid value '%s'. Expected type: %s.", param, value, expectedType),
                HttpStatus.BAD_REQUEST.value(),
                Instant.now().toString()
        );

        return ResponseEntity.badRequest().body(errorResponse);
    }
}