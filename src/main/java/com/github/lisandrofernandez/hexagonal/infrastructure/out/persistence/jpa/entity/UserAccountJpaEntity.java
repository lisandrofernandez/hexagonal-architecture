package com.github.lisandrofernandez.hexagonal.infrastructure.out.persistence.jpa.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import java.util.UUID;

@Entity
@Table(name = "user_account")
@Getter @Setter
public class UserAccountJpaEntity {

    @Id
    private UUID id;

    private String username;

    private String lowercasedUsername;

    private String name;

    @Version
    private Integer version;

    // fluent API

    public UserAccountJpaEntity id(UUID id) {
        setId(id);
        return this;
    }

    public UUID id() {
        return getId();
    }

    public UserAccountJpaEntity username(String username) {
        setUsername(username);
        return this;
    }

    public String username() {
        return getUsername();
    }

    public UserAccountJpaEntity lowercasedUsername(String lowercasedUsername) {
        setLowercasedUsername(lowercasedUsername);
        return this;
    }

    public String lowercasedUsername() {
        return getLowercasedUsername();
    }

    public UserAccountJpaEntity name(String name) {
        setName(name);
        return this;
    }

    public String name() {
        return getName();
    }
}
