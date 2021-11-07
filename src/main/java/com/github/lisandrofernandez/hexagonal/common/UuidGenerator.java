package com.github.lisandrofernandez.hexagonal.common;

import java.util.UUID;

@FunctionalInterface
public interface UuidGenerator {
    UUID generateUuid();
}
