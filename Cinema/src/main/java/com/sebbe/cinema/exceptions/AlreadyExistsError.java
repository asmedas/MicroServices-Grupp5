package com.sebbe.cinema.exceptions;

public class AlreadyExistsError extends RuntimeException {
    public AlreadyExistsError(String message) {
        super(message);
    }
}
