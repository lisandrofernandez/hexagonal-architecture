package com.github.lisandrofernandez.hexagonal.domain.port.in;

import com.github.lisandrofernandez.hexagonal.domain.model.UserAccount;

import java.util.Optional;
import java.util.UUID;

// primary port
public interface GetUserAccountQuery {
    Optional<UserAccount> getUserAccountById(UUID id);
}
