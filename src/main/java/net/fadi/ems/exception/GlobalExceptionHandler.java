package net.fadi.ems.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import net.fadi.ems.model.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException exception,
                        HttpServletRequest request) {
                ErrorResponse errorResponse = new ErrorResponse(
                                "Not found",
                                exception.getMessage(),
                                request.getRequestURI(),
                                HttpStatus.NOT_FOUND.value());

                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        @ExceptionHandler(DatabaseException.class)
        public ResponseEntity<ErrorResponse> handleDatabaseException(DatabaseException exception,
                        HttpServletRequest request) {
                ErrorResponse errorResponse = new ErrorResponse(
                                "Database error",
                                exception.getMessage(),
                                request.getRequestURI(),
                                HttpStatus.BAD_REQUEST.value());

                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(UsernameOrEmailNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleUsernameOrEmailNotFoundException(
                        UsernameOrEmailNotFoundException exception,
                        HttpServletRequest request) {
                ErrorResponse errorResponse = new ErrorResponse(
                                "Username or email not found",
                                exception.getMessage(),
                                request.getRequestURI(),
                                HttpStatus.NOT_FOUND.value());

                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        @ExceptionHandler(RegisterException.class)
        public ResponseEntity<ErrorResponse> handleRegisterException(RegisterException exception,
                        HttpServletRequest request) {
                ErrorResponse errorResponse = new ErrorResponse(
                                "Registration error",
                                exception.getMessage(),
                                request.getRequestURI(),
                                HttpStatus.BAD_REQUEST.value());

                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(LoginException.class)
        public ResponseEntity<ErrorResponse> handleLoginException(LoginException exception,
                        HttpServletRequest request) {
                ErrorResponse errorResponse = new ErrorResponse(
                                "Login error",
                                exception.getMessage(),
                                request.getRequestURI(),
                                HttpStatus.BAD_REQUEST.value());

                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException exception,
                        HttpServletRequest request) {
                Map<String, String> errors = new HashMap<>();

                // Extract field names and the specific validation messages
                exception.getBindingResult().getFieldErrors()
                                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

                ErrorResponse errorResponse = new ErrorResponse(
                                "Validation Failed",
                                errors.toString(),
                                request.getRequestURI(),
                                HttpStatus.BAD_REQUEST.value());

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
}
