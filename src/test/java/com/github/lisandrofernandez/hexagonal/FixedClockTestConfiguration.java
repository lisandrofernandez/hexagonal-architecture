package com.github.lisandrofernandez.hexagonal;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

@TestConfiguration
public class FixedClockTestConfiguration {

    @Bean
    public Clock clock() {
        return Clock.fixed(Instant.parse("2021-10-30T10:00:30.126Z"), ZoneOffset.UTC);
    }
}
