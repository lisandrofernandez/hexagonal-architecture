package com.github.lisandrofernandez.hexagonal.domain.port.out.repository;

import com.github.lisandrofernandez.hexagonal.domain.model.UserAccount;

import java.util.Optional;
import java.util.UUID;

// secondary port
public interface UserAccountRepository {

    UserAccount create(UserAccount userAccount);

    Optional<UserAccount> findById(UUID id);

    Optional<UserAccount> findByUsername(String username);

    boolean existsByUsername(String username);

    Optional<UserAccount> deleteByUsernameAndReturnDeleted(String username);
}
