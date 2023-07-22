package com.github.lisandrofernandez.hexagonal.domain.port.out.messaging;

import com.github.lisandrofernandez.hexagonal.domain.port.out.command.SendUserAccountMessageCommand;

import java.util.UUID;

public interface FctUserAccountMessageProducer {

    void sendUserAccount(SendUserAccountMessageCommand sendUserAccountMessageCommand);

    void sendDeletedUserAccount(UUID userAccountId);
}
