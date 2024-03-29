package com.github.lisandrofernandez.hexagonal.infrastructure.out.persistence;

import com.github.lisandrofernandez.hexagonal.common.util.Assert;
import com.github.lisandrofernandez.hexagonal.domain.model.UserAccount;
import com.github.lisandrofernandez.hexagonal.domain.port.out.repository.UserAccountRepository;
import com.github.lisandrofernandez.hexagonal.infrastructure.out.persistence.jpa.entity.UserAccountJpaEntity;
import com.github.lisandrofernandez.hexagonal.infrastructure.out.persistence.jpa.repository.UserAccountJpaRepository;
import com.github.lisandrofernandez.hexagonal.infrastructure.out.persistence.mapper.UserAccountJpaEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserAccountPersistenceAdapter implements UserAccountRepository {

    public static final String USERNAME_MUST_NOT_BE_NULL = "username must not be null";

    private final UserAccountJpaRepository userAccountJpaRepository;
    private final UserAccountJpaEntityMapper userAccountJpaEntityMapper;

    @Override
    public UserAccount create(UserAccount userAccount) {
        Assert.notNull(userAccount, "userAccount must not be null");

        UserAccountJpaEntity userAccountJpaEntity = userAccountJpaRepository.save(
                userAccountJpaEntityMapper.toEntity(userAccount)
        );
        return userAccountJpaEntityMapper.toDomain(userAccountJpaEntity);
    }

    @Override
    public Optional<UserAccount> findById(UUID id) {
        Assert.notNull(id, "id must not be null");

        return userAccountJpaRepository.findById(id)
                .map(userAccountJpaEntityMapper::toDomain);
    }

    @Override
    public Optional<UserAccount> findByUsername(String username) {
        Assert.notNull(username, USERNAME_MUST_NOT_BE_NULL);

        return userAccountJpaRepository.findByLowercasedUsername(username.toLowerCase(Locale.ROOT))
                .map(userAccountJpaEntityMapper::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        Assert.notNull(username, USERNAME_MUST_NOT_BE_NULL);

        return userAccountJpaRepository.existsByLowercasedUsername(username.toLowerCase(Locale.ROOT));
    }

    @Override
    @Transactional
    public Optional<UserAccount> deleteByUsernameAndReturnDeleted(String username) {
        Assert.notNull(username, USERNAME_MUST_NOT_BE_NULL);

        return userAccountJpaRepository.findByLowercasedUsername(username.toLowerCase(Locale.ROOT))
                .map(userAccountJpaEntity -> {
                    userAccountJpaRepository.delete(userAccountJpaEntity);
                    return userAccountJpaEntityMapper.toDomain(userAccountJpaEntity);
                });
    }
}
