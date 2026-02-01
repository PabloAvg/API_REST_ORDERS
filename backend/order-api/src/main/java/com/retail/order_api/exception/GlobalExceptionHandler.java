package com.retail.order_api.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> notFound(NotFoundException ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> badRequest(BadRequestException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiError> validation(MethodArgumentNotValidException ex, HttpServletRequest req) {

            String fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));

            String globalErrors = ex.getBindingResult().getGlobalErrors().stream()
                .map(oe -> oe.getObjectName() + ": " + oe.getDefaultMessage())
                .collect(Collectors.joining("; "));

            String msg = Stream.of(fieldErrors, globalErrors)
                .filter(s -> s != null && !s.isBlank())
                .collect(Collectors.joining("; "));

            return build(HttpStatus.BAD_REQUEST, msg, req.getRequestURI());
        }

    private ResponseEntity<ApiError> build(HttpStatus status, String message, String path) {
        return ResponseEntity.status(status).body(
                new ApiError(Instant.now(), status.value(), status.getReasonPhrase(), message, path)
        );
    }
}
