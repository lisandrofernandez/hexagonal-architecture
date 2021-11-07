package com.github.lisandrofernandez.hexagonal.common;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class JdkUuidGenerator implements UuidGenerator {

    @Override
    public UUID generateUuid() {
        return UUID.randomUUID();
    }
}
