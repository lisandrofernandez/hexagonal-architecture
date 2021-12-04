package com.github.lisandrofernandez.hexagonal.domain.port.out.command;

import com.github.lisandrofernandez.hexagonal.domain.DomainException.Assert;
import com.github.lisandrofernandez.hexagonal.domain.model.UserAccount;
import com.github.lisandrofernandez.hexagonal.domain.port.out.messaging.EventType;
import lombok.Builder;
import lombok.Value;

@Value
public class SendUserAccountMessageCommand {
    EventType eventType;
    UserAccount userAccount;

    @Builder(toBuilder = true)
    SendUserAccountMessageCommand(EventType eventType, UserAccount userAccount) {
        this.eventType = Assert.notNull(eventType, "eventType must not be null");
        this.userAccount = Assert.notNull(userAccount, "userAccount must not be null");
    }
}
