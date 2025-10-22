package com.zetterlund.wigell_sushi_api.exception;

public class AlreadyExistsError extends RuntimeException {
    public AlreadyExistsError(String message) {
        super(message);
    }
}
