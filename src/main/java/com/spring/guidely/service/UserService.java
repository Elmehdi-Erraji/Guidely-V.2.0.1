package com.spring.guidely.service;

import com.spring.guidely.entities.AppUser;
import com.spring.guidely.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AppUser save(AppUser user) {
        return userRepository.save(user);
    }

    public AppUser Update(AppUser user) {
        return userRepository.save(user);
    }

    public Optional<AppUser>  getUserById(UUID id) {
        return userRepository.findById(id);
    }

    public Page<AppUser> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public void delete(UUID id) {
        userRepository.deleteById(id);
    }
}
