package com.github.lisandrofernandez.hexagonal.domain.service;

import com.github.lisandrofernandez.hexagonal.common.UuidGenerator;
import com.github.lisandrofernandez.hexagonal.common.stereotype.UseCase;
import com.github.lisandrofernandez.hexagonal.common.util.Assert;
import com.github.lisandrofernandez.hexagonal.domain.model.UserAccount;
import com.github.lisandrofernandez.hexagonal.domain.port.in.CreateUserAccountHandler;
import com.github.lisandrofernandez.hexagonal.domain.port.in.command.CreateUserAccountCommand;
import com.github.lisandrofernandez.hexagonal.domain.port.out.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class CreateUserAccountService implements CreateUserAccountHandler {
    private final UuidGenerator uuidGenerator;
    private final UserAccountRepository userAccountRepository;

    @Override
    public UserAccount createUserAccount(CreateUserAccountCommand createUserAccountCommand) {
        Assert.notNull(createUserAccountCommand, "createUserAccountCommand must not be null");

        UserAccount userAccount = UserAccount.builder()
                .id(uuidGenerator.generateUuid())
                .username(createUserAccountCommand.getUsername())
                .name(createUserAccountCommand.getName())
                .build();

        String username = userAccount.getUsername();
        if (userAccountRepository.existsByUsername(username)) {
            throw new UserAccountExitsException("user account " + username + " already exists");
        }

        return userAccountRepository.create(userAccount);
    }
}
