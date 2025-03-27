package com.medals.medalsbackend.exception;

public class CsvLoadingException extends RuntimeException {
    public CsvLoadingException(String message, Throwable cause) {
        super(message, cause);
    }
}
