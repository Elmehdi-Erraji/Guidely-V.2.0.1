package com.spring.guidely.service;

import com.spring.guidely.entities.AppUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    AppUser save(AppUser user);
    AppUser update(AppUser user);
    Optional<AppUser> getUserById(UUID id);
    Page<AppUser> getAllUsers(Pageable pageable);
    void delete(UUID id);
}
