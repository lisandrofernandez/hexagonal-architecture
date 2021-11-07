package com.github.lisandrofernandez.hexagonal.domain.port.in.command;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder(toBuilder = true)
public class CreateUserAccountCommand {
    private final String username;
    private final String name;
}
