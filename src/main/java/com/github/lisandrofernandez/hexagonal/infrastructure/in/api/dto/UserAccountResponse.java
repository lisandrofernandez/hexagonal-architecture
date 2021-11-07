package com.github.lisandrofernandez.hexagonal.infrastructure.in.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
public class UserAccountResponse {
    private UUID id;
    private String username;
    private String name;


    // fluent API

    public UserAccountResponse id(UUID id) {
        setId(id);
        return this;
    }

    public UserAccountResponse username(String username) {
        setUsername(username);
        return this;
    }

    public UserAccountResponse name(String name) {
        setName(name);
        return this;
    }
}
