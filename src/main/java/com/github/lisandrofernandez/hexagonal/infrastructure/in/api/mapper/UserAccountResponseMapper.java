package com.github.lisandrofernandez.hexagonal.infrastructure.in.api.mapper;

import com.github.lisandrofernandez.hexagonal.common.util.Assert;
import com.github.lisandrofernandez.hexagonal.domain.model.UserAccount;
import com.github.lisandrofernandez.hexagonal.infrastructure.in.api.dto.UserAccountResponse;
import org.springframework.stereotype.Component;

@Component
public class UserAccountResponseMapper {

    public UserAccountResponse fromDomain(UserAccount from) {
        Assert.notNull(from, "user account must not be null");

        return new UserAccountResponse()
                .id(from.getId())
                .username(from.getUsername())
                .name(from.getName());
    }
}
