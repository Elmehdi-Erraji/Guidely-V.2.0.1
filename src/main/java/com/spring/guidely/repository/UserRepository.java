package com.spring.guidely.repository;

import com.spring.guidely.entities.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository

public interface UserRepository extends JpaRepository<AppUser, UUID> {
    Optional<AppUser> findById(UUID id);
    Optional<AppUser> findByEmail(String email);
}
