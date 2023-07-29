package com.github.lisandrofernandez.hexagonal.domain.usecase;

import com.github.lisandrofernandez.hexagonal.domain.DomainException;

public class UserAccountExitsException extends DomainException {
    static final long serialVersionUID = 5824774777536798311L;

    public UserAccountExitsException() {
    }

    public UserAccountExitsException(String message) {
        super(message);
    }
}
