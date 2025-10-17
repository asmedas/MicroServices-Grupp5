package com.sebbe.cinema.exceptions;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.persistence.PersistenceException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation ->
                errors.put(violation.getPropertyPath().toString(), violation.getMessage())
        );
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AlreadyExistsError.class)
    public ResponseEntity<?> handleAlreadyExists(AlreadyExistsError ex){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                Map.of(
                        "timestamp", LocalDateTime.now().toString(),
                        "status", HttpStatus.CONFLICT,
                        "error", "Already exists",
                        "message", ex.getMessage()
                )
        );
    }

    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<?> handleInvalidFormat(InvalidFormatException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Map.of(
                        "timestamp", LocalDateTime.now().toString(),
                        "status", HttpStatus.BAD_REQUEST,
                        "error", "Invalid format",
                        "message", ex.getMessage()
                )
        );
    }

    @ExceptionHandler(NoMatchException.class)
    public ResponseEntity<?> handleNoMatch(NoMatchException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of(
                        "timestamp", LocalDateTime.now().toString(),
                        "status", HttpStatus.NOT_FOUND,
                        "error", "No match",
                        "message", ex.getMessage()
                )
        );
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> handleIllegalState(IllegalStateException exception){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Map.of(
                        "timestamp", LocalDateTime.now().toString(),
                        "status", HttpStatus.BAD_REQUEST,
                        "error", "Bad Request",
                        "message", exception.getMessage()
                )
        );
    }

    @ExceptionHandler(UnexpectedError.class)
    public ResponseEntity<?> handleUnexpectedError(UnexpectedError ex){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                Map.of(
                        "timestamp", LocalDateTime.now().toString(),
                        "status", HttpStatus.INTERNAL_SERVER_ERROR,
                        "error", "Unexpected error",
                        "message", ex.getMessage()
                )
        );
    }

    @ExceptionHandler(PersistenceException.class)
    public ResponseEntity<?> handlePersistenceException(PersistenceException ex){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                Map.of(
                        "timestamp", LocalDateTime.now().toString(),
                        "status", HttpStatus.INTERNAL_SERVER_ERROR,
                        "error", "Persistence error",
                        "message", ex.getMessage()
                )
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Map.of(
                        "timestamp", LocalDateTime.now().toString(),
                        "status", HttpStatus.BAD_REQUEST,
                        "error", "Bad Request",
                        "message", ex.getMessage()
                )
        );
    }

}
