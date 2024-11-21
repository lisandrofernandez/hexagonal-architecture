package com.github.lisandrofernandez.hexagonal.domain.usecase;

import com.github.lisandrofernandez.hexagonal.domain.DomainException;

import java.io.Serial;

public class UserAccountExitsException extends DomainException {

    @Serial
    private static final long serialVersionUID = 5824774777536798311L;

    public UserAccountExitsException() {
    }

    public UserAccountExitsException(String message) {
        super(message);
    }
}
