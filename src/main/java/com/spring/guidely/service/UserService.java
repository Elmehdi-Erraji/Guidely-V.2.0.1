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
    private final EmailService emailService;

    public UserService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    public AppUser save(AppUser user) {
        AppUser createdUser = userRepository.save(user);

        // Prepare email content. Adjust formatting as needed.
        String subject = "Your Account Credentials";
        String content = "<p>Dear " + createdUser.getName() + ",</p>" +
                "<p>Your account has been created successfully.</p>" +
                "<p><strong>Login Email:</strong> " + createdUser.getEmail() + "</p>" +
                "<p><strong>Password:</strong> " + createdUser.getPassword() + "</p>" +
                "<p>Please change your password upon first login.</p>";

        // Send the email
        emailService.sendEmail(createdUser.getEmail(), subject, content);

        return createdUser;
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
