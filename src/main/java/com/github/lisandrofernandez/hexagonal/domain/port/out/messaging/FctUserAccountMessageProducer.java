package com.github.lisandrofernandez.hexagonal.domain.port.out.messaging;

import com.github.lisandrofernandez.hexagonal.domain.port.out.command.SendUserAccountMessageCommand;

public interface FctUserAccountMessageProducer {

    void sendUserAccount(SendUserAccountMessageCommand sendUserAccountMessageCommand);
}
