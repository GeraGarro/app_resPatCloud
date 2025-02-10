package com.appResP.residuosPatologicos.exceptions;

public class UniqueConstraintViolationException extends RuntimeException {

    public UniqueConstraintViolationException() {
        super();
    }

    public UniqueConstraintViolationException(String message) {
        super(message);
    }

    public UniqueConstraintViolationException(String message, Throwable cause) {
        super(message, cause);
    }

    public UniqueConstraintViolationException(Throwable cause) {
        super(cause);
    }
}

