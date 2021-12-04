package com.github.lisandrofernandez.hexagonal.domain.service;

import com.github.lisandrofernandez.hexagonal.common.stereotype.UseCase;
import com.github.lisandrofernandez.hexagonal.common.util.Assert;
import com.github.lisandrofernandez.hexagonal.domain.model.UserAccount;
import com.github.lisandrofernandez.hexagonal.domain.port.in.DeleteUserAccountByUsernameHandler;
import com.github.lisandrofernandez.hexagonal.domain.port.out.command.SendUserAccountMessageCommand;
import com.github.lisandrofernandez.hexagonal.domain.port.out.messaging.EventType;
import com.github.lisandrofernandez.hexagonal.domain.port.out.messaging.FctUserAccountMessageProducer;
import com.github.lisandrofernandez.hexagonal.domain.port.out.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@UseCase
@Slf4j
@RequiredArgsConstructor
public class DeleteUserAccountByUsernameService implements DeleteUserAccountByUsernameHandler {
    private final UserAccountRepository userAccountRepository;
    private final FctUserAccountMessageProducer fctUserAccountMessageProducer;

    @Override
    public boolean delete(String username) {
        Assert.notNull(username, "username must not be null");

        Optional<UserAccount> optionalUserAccount = userAccountRepository.deleteByUsernameAndReturnDeleted(username);
        optionalUserAccount.ifPresent(this::sendDeletedUserAccountMessage);

        return optionalUserAccount.isPresent();
    }

    private void sendDeletedUserAccountMessage(UserAccount userAccount) {
        SendUserAccountMessageCommand command = SendUserAccountMessageCommand.builder()
                .eventType(EventType.DELETE)
                .userAccount(userAccount)
                .build();
        try {
            fctUserAccountMessageProducer.sendUserAccount(command);
        } catch (Exception e) {
            log.error("Error sending user account message", e);
        }
    }
}
