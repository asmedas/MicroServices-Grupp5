package com.sebbe.cinema.exceptions;

public class UnexpectedError extends RuntimeException {
    public UnexpectedError(String message) {
        super(message);
    }
}
