package com.github.lisandrofernandez.hexagonal.infrastructure.out.persistence.jpa.repository;

import com.github.lisandrofernandez.hexagonal.infrastructure.out.persistence.jpa.entity.UserAccountJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserAccountJpaRepository extends JpaRepository<UserAccountJpaEntity, UUID> {

    Optional<UserAccountJpaEntity> findByLowercasedUsername(String lowercasedUsername);

    boolean existsByLowercasedUsername(String lowercasedUsername);
}
