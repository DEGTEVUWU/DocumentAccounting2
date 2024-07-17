package com.ivan_degtev.documentaccounting2.handlers;


import com.ivan_degtev.documentaccounting2.exceptions.NotFoundException;
import com.ivan_degtev.documentaccounting2.exceptions.ResourceNotValidException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@ControllerAdvice
public class GlobalControllerExceptionHandler {

    @ExceptionHandler(DataAccessException.class)
    public ResponseStatusException handleDataAccessException(DataAccessException ex) {
        return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Server error, please try again later");
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseStatusException handleNotFoundException(NotFoundException ex) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseStatusException handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        List<String> errors = fieldErrors.stream()
                .map(FieldError::getDefaultMessage)
                .toList();
        String errorMessage = errors
                .toString().replace("[", "")
                .replace("]", "");
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseStatusException handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, "There is already a registered user with this username or email address");
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseStatusException handleBadCredentialsException(BadCredentialsException ex) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad credentials");
    }
    @ExceptionHandler(ResourceNotValidException.class)
    public ResponseEntity<String> handleResourceNotValidException(ResourceNotValidException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
