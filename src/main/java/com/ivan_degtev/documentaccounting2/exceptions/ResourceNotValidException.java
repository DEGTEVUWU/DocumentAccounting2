package com.ivan_degtev.documentaccounting2.exceptions;

public class ResourceNotValidException extends RuntimeException {
    public ResourceNotValidException(String message) {
        super(message);
    }
}