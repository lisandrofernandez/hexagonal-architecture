package com.github.lisandrofernandez.hexagonal.infrastructure;

public class InfrastructureException extends RuntimeException {
    static final long serialVersionUID = 4399148820669444290L;

    public InfrastructureException() {
    }

    public InfrastructureException(String message) {
        super(message);
    }

    public InfrastructureException(String message, Throwable cause) {
        super(message, cause);
    }

    public InfrastructureException(Throwable cause) {
        super(cause);
    }
}
