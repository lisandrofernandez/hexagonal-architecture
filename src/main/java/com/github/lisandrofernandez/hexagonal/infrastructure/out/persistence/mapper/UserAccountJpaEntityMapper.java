package com.github.lisandrofernandez.hexagonal.infrastructure.out.persistence.mapper;

import com.github.lisandrofernandez.hexagonal.common.util.Assert;
import com.github.lisandrofernandez.hexagonal.domain.model.UserAccount;
import com.github.lisandrofernandez.hexagonal.infrastructure.out.persistence.jpa.entity.UserAccountJpaEntity;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class UserAccountJpaEntityMapper {

    public UserAccount toDomain(UserAccountJpaEntity from) {
        Assert.notNull(from, "UserAccountJpaEntity must not be null");

        return UserAccount.builder()
                .id(from.id())
                .username(from.username())
                .name(from.name())
                .build();
    }

    public UserAccountJpaEntity toEntity(UserAccount from) {
        Assert.notNull(from, "UserAccount must not be null");

        return new UserAccountJpaEntity()
                .id(from.getId())
                .username(from.getUsername())
                .lowercasedUsername(from.getUsername().toLowerCase(Locale.ROOT))
                .name(from.getName());
    }
}
