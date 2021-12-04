package com.github.lisandrofernandez.hexagonal.infrastructure.out.messaging.mapper;

import com.github.lisandrofernandez.hexagonal.common.util.Assert;
import com.github.lisandrofernandez.hexagonal.domain.model.UserAccount;
import com.github.lisandrofernandez.hexagonal.infrastructure.out.messaging.payload.UserAccountPayload;
import org.springframework.stereotype.Component;

@Component
public class UserAccountPayloadMapper {

    public UserAccountPayload toPayload(UserAccount from) {
        Assert.notNull(from, "UserAccount must not be null");

        return UserAccountPayload.builder()
                .id(from.getId())
                .name(from.getName())
                .username(from.getUsername())
                .build();
    }
}
