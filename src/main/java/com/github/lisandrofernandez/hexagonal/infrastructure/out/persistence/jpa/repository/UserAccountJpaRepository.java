package com.github.lisandrofernandez.hexagonal.infrastructure.out.persistence.jpa.repository;

import com.github.lisandrofernandez.hexagonal.infrastructure.out.persistence.jpa.entity.UserAccountJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

public interface UserAccountJpaRepository extends JpaRepository<UserAccountJpaEntity, UUID> {

    @Transactional(readOnly = true)
    Optional<UserAccountJpaEntity> findByLowercasedUsername(String lowercasedUsername);

    @Transactional(readOnly = true)
    boolean existsByLowercasedUsername(String lowercasedUsername);
}
