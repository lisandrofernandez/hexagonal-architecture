package com.github.lisandrofernandez.hexagonal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class HexagonalArchitectureConfig {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
