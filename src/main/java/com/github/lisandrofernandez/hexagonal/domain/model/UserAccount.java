package com.github.lisandrofernandez.hexagonal.domain.model;

import lombok.Builder;
import lombok.Value;
import org.apache.commons.lang3.CharUtils;

import java.util.UUID;

import static com.github.lisandrofernandez.hexagonal.domain.DomainException.Assert;

@Value
public class UserAccount {
    UUID id;
    String username;
    String name;

    @Builder(toBuilder = true)
    UserAccount(UUID id, String username, String name) {
        this.id = Assert.notNull(id, "id must not be null");
        this.username = validateUsername(username);
        this.name = validateName(name);
    }

    private String validateUsername(String username) {
        Assert.notNull(username, "username must not be null");
        Assert.isTrue(!username.isEmpty(), "username must not be empty");
        int length = username.length();
        Assert.isTrue(length >= 3, "username must have at least 3 characters");
        Assert.isTrue(length <= 50, "username must not exceed 50 characters");
        Assert.isTrue(CharUtils.isAsciiAlpha(username.charAt(0)), "username must start with a letter");
        for (int i = 1; i < length; i++) {
            char ch = username.charAt(i);
            Assert.isTrue(
                    CharUtils.isAsciiAlphanumeric(ch) || ch == '.' || ch == '-' || ch == '_',
                    "invalid username character"
            );
        }
        return username;
    }

    private String validateName(String name) {
        Assert.notNull(name, "name must not be null");
        Assert.isTrue(!name.isEmpty(), "name must not be empty");
        int length = name.length();
        Assert.isTrue(length <= 300, "username must not exceed 300 characters");
        Assert.isTrue(!Character.isWhitespace(name.charAt(0)), "invalid name");
        Assert.isTrue(!Character.isWhitespace(name.charAt(length - 1)), "invalid name");
        for (int i = 0; i < length; i++) {
            Assert.isTrue(!Character.isISOControl(name.charAt(i)), "invalid name");
        }
        return name;
    }
}
