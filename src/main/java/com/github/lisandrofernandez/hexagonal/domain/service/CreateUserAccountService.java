package com.github.lisandrofernandez.hexagonal.domain.service;

import com.github.lisandrofernandez.hexagonal.common.UuidGenerator;
import com.github.lisandrofernandez.hexagonal.common.stereotype.UseCase;
import com.github.lisandrofernandez.hexagonal.common.util.Assert;
import com.github.lisandrofernandez.hexagonal.domain.model.UserAccount;
import com.github.lisandrofernandez.hexagonal.domain.port.in.CreateUserAccountHandler;
import com.github.lisandrofernandez.hexagonal.domain.port.in.command.CreateUserAccountCommand;
import com.github.lisandrofernandez.hexagonal.domain.port.out.command.SendUserAccountMessageCommand;
import com.github.lisandrofernandez.hexagonal.domain.port.out.messaging.EventType;
import com.github.lisandrofernandez.hexagonal.domain.port.out.messaging.FctUserAccountMessageProducer;
import com.github.lisandrofernandez.hexagonal.domain.port.out.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@UseCase
@Slf4j
@RequiredArgsConstructor
public class CreateUserAccountService implements CreateUserAccountHandler {
    private final UuidGenerator uuidGenerator;
    private final UserAccountRepository userAccountRepository;
    private final FctUserAccountMessageProducer fctUserAccountMessageProducer;

    @Override
    public UserAccount createUserAccount(CreateUserAccountCommand createUserAccountCommand) {
        Assert.notNull(createUserAccountCommand, "createUserAccountCommand must not be null");

        UserAccount userAccount = buildUserAccount(createUserAccountCommand);
        checkIfUserAccountExistsByUsername(userAccount.getUsername());
        userAccount = userAccountRepository.create(userAccount);
        sendCreatedUserAccountMessage(userAccount);

        return userAccount;
    }

    private UserAccount buildUserAccount(CreateUserAccountCommand createUserAccountCommand) {
        return UserAccount.builder()
                .id(uuidGenerator.generateUuid())
                .username(createUserAccountCommand.getUsername())
                .name(createUserAccountCommand.getName())
                .build();
    }

    private void checkIfUserAccountExistsByUsername(String username) {
        if (userAccountRepository.existsByUsername(username)) {
            throw new UserAccountExitsException("user account " + username + " already exists");
        }
    }

    private void sendCreatedUserAccountMessage(UserAccount userAccount) {
        SendUserAccountMessageCommand command = SendUserAccountMessageCommand.builder()
                .eventType(EventType.CREATE)
                .userAccount(userAccount)
                .build();
        try {
            fctUserAccountMessageProducer.sendUserAccount(command);
        } catch (Exception e) {
            log.error("Error sending created user account message | user account ID: {}", userAccount.getId(), e);
        }
    }
}
