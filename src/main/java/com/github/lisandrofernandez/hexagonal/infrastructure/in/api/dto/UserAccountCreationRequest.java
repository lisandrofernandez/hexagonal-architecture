package com.github.lisandrofernandez.hexagonal.infrastructure.in.api.dto;

import lombok.Data;

@Data
public class UserAccountCreationRequest {
    private String username;
    private String name;

    // fluent API

    public UserAccountCreationRequest username(String username) {
        setUsername(username);
        return this;
    }

    public String username() {
        return getUsername();
    }

    public String name() {
        return getName();
    }

    public UserAccountCreationRequest name(String name) {
        setName(name);
        return this;
    }
}
