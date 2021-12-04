package com.github.lisandrofernandez.hexagonal.infrastructure.in.messaging;

import com.github.lisandrofernandez.hexagonal.infrastructure.InfrastructureException;

public class IncomingMessagingException extends InfrastructureException {
    static final long serialVersionUID = 1401029841067601560L;

    public IncomingMessagingException() {
    }

    public IncomingMessagingException(String message) {
        super(message);
    }

    public IncomingMessagingException(String message, Throwable cause) {
        super(message, cause);
    }

    public IncomingMessagingException(Throwable cause) {
        super(cause);
    }
}
