package com.zetterlund.wigell_sushi_api.exception;

public class UnexpectedError extends RuntimeException {
    public UnexpectedError(String message) {
        super(message);
    }
}
