package com.redcare.popularity.error;

import com.redcare.popularity.dto.ErrorResponseDto;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDto> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String param = ex.getName();
        Object paramValue = ex.getValue();
        String value = paramValue != null ? paramValue.toString() : "null";
        Class<?> requiredType = ex.getRequiredType();
        String expectedType = requiredType != null ? requiredType.getSimpleName() : "unknown";

        ErrorResponseDto errorResponse = new ErrorResponseDto(
                "Invalid request parameter",
                String.format("Parameter '%s' has invalid value '%s'. Expected type: %s.", param, value, expectedType),
                HttpStatus.BAD_REQUEST.value(),
                Instant.now().toString()
        );

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ErrorResponseDto> handleRateLimitExceeded(RateLimitExceededException ex) {
        ErrorResponseDto errorResponse = new ErrorResponseDto(
                "Rate limit exceeded",
                ex.getMessage() != null ? ex.getMessage() : "GitHub API rate limit exceeded. Please try again later.",
                HttpStatus.TOO_MANY_REQUESTS.value(),
                Instant.now().toString()
        );

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(errorResponse);
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorResponseDto> handleFeignException(FeignException ex) {
        String message = "Error communicating with GitHub API";
        if (ex instanceof FeignException.ServiceUnavailable) {
            message = "GitHub API is currently unavailable. Please try again later.";
        } else if (ex instanceof FeignException.InternalServerError) {
            message = "GitHub API returned an internal server error. Please try again later.";
        } else if (ex.getMessage() != null && ex.getMessage().contains("Connection refused")) {
            message = "Cannot connect to GitHub API. The service may be down.";
        }

        ErrorResponseDto errorResponse = new ErrorResponseDto(
                "GitHub API communication error",
                message,
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                Instant.now().toString()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}