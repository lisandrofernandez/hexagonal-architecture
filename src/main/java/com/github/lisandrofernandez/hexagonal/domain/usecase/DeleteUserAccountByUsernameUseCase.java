package com.github.lisandrofernandez.hexagonal.domain.usecase;

import com.github.lisandrofernandez.hexagonal.common.stereotype.UseCase;
import com.github.lisandrofernandez.hexagonal.common.util.Assert;
import com.github.lisandrofernandez.hexagonal.domain.model.UserAccount;
import com.github.lisandrofernandez.hexagonal.domain.port.in.DeleteUserAccountByUsernameHandler;
import com.github.lisandrofernandez.hexagonal.domain.port.out.messaging.FctUserAccountMessageProducer;
import com.github.lisandrofernandez.hexagonal.domain.port.out.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@UseCase
@Slf4j
@RequiredArgsConstructor
public class DeleteUserAccountByUsernameUseCase implements DeleteUserAccountByUsernameHandler {
    private final UserAccountRepository userAccountRepository;
    private final FctUserAccountMessageProducer fctUserAccountMessageProducer;

    @Override
    public boolean delete(String username) {
        Assert.notNull(username, "username must not be null");

        UserAccount deletedUserAccount = userAccountRepository.deleteByUsernameAndReturnDeleted(username)
                .orElse(null);

        if (deletedUserAccount == null) {
            return false;
        }

        sendDeletedUserAccountMessage(deletedUserAccount);
        return true;
    }

    private void sendDeletedUserAccountMessage(UserAccount userAccount) {
        try {
            fctUserAccountMessageProducer.sendDeletedUserAccount(userAccount.getId());
        } catch (Exception e) {
            log.error("Error sending deleted user account message | user account ID: {}", userAccount.getId(), e);
        }
    }
}
