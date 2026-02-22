package net.fadi.ems.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
}
