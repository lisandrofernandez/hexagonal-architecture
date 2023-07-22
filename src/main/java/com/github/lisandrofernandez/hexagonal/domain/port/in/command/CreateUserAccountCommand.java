package com.github.lisandrofernandez.hexagonal.domain.port.in.command;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class CreateUserAccountCommand {
    String username;
    String name;
}
