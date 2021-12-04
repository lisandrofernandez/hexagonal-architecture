package com.github.lisandrofernandez.hexagonal.infrastructure.out.messaging.payload;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder(toBuilder = true)
public class UserAccountPayload {
    UUID id;
    String username;
    String name;
}
