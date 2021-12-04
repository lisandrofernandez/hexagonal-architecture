package com.github.lisandrofernandez.hexagonal.infrastructure.in.messaging.payload;

import lombok.Data;

@Data
public class CmdUserAccountPayload {
    private String username;

    // fluent API

    public CmdUserAccountPayload username(String username) {
        setUsername(username);
        return this;
    }

    public String username() {
        return getUsername();
    }
}
