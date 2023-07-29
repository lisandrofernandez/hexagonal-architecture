package com.github.lisandrofernandez.hexagonal.domain.usecase;

import com.github.lisandrofernandez.hexagonal.common.stereotype.UseCase;
import com.github.lisandrofernandez.hexagonal.common.util.Assert;
import com.github.lisandrofernandez.hexagonal.domain.model.UserAccount;
import com.github.lisandrofernandez.hexagonal.domain.port.in.query.GetUserAccountQuery;
import com.github.lisandrofernandez.hexagonal.domain.port.out.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.UUID;

@UseCase
@RequiredArgsConstructor
public class GetUserAccountUseCase implements GetUserAccountQuery {
    private final UserAccountRepository userAccountRepository;

    @Override
    public Optional<UserAccount> getUserAccountById(UUID id) {
        Assert.notNull(id, "id must not be null");

        return userAccountRepository.findById(id);
    }
}
