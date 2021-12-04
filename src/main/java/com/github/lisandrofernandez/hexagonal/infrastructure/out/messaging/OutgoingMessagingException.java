package com.github.lisandrofernandez.hexagonal.infrastructure.out.messaging;

import com.github.lisandrofernandez.hexagonal.infrastructure.InfrastructureException;

public class OutgoingMessagingException extends InfrastructureException {
    static final long serialVersionUID = 1962825836211187900L;

    public OutgoingMessagingException() {
    }

    public OutgoingMessagingException(String message) {
        super(message);
    }

    public OutgoingMessagingException(String message, Throwable cause) {
        super(message, cause);
    }

    public OutgoingMessagingException(Throwable cause) {
        super(cause);
    }
}
