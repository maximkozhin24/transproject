package com.logistics.logisticsapp.exception;

import com.logistics.logisticsapp.service.OrderService;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import com.logistics.logisticsapp.dto.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@ControllerAdvice(basePackages = "com.logistics.logisticsapp")
public class GlobalExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobal(Exception ex) {

        ErrorResponse error = new ErrorResponse(
            "Internal server error",
            500,
            "INTERNAL_ERROR"
        );

        LOG.error("500 INTERNAL_ERROR: {}", ex.getMessage(), ex);

        return ResponseEntity.status(500).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {

        String message = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(err -> err.getField() + ": " + err.getDefaultMessage())
            .findFirst()
            .orElse("Validation error");

        ErrorResponse error = new ErrorResponse(
            message,
            400,
            "VALIDATION_ERROR"
        );

        LOG.warn("400 VALIDATION_ERROR: {}", message);

        return ResponseEntity.badRequest().body(error);
    }
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException ex) {

        ErrorResponse error = new ErrorResponse(
            ex.getMessage(),
            404,
            "NOT_FOUND"
        );

        LOG.warn("404 NOT_FOUND: {}", ex.getMessage());

        return ResponseEntity.status(404).body(error);
    }
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(ConflictException ex) {

        ErrorResponse error = new ErrorResponse(
            ex.getMessage(),
            409,
            "CONFLICT"
        );

        LOG.warn("409 CONFLICT: {}", ex.getMessage());

        return ResponseEntity.status(409).body(error);
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleBadJson(HttpMessageNotReadableException ex) {

        ErrorResponse error = new ErrorResponse(
            "Invalid or missing request body fields",
            400,
            "VALIDATION_ERROR"
        );
        LOG.warn("400 VALIDATION_ERROR: {}", "Invalid or missing request body fields");

        return ResponseEntity.badRequest().body(error);
    }
}






