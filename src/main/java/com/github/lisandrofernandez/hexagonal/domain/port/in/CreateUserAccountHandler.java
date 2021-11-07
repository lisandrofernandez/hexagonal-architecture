package com.github.lisandrofernandez.hexagonal.domain.port.in;

import com.github.lisandrofernandez.hexagonal.domain.model.UserAccount;
import com.github.lisandrofernandez.hexagonal.domain.port.in.command.CreateUserAccountCommand;

public interface CreateUserAccountHandler {
    UserAccount createUserAccount(CreateUserAccountCommand createUserAccountCommand);
}
