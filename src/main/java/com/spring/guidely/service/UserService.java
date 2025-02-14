package com.spring.guidely.service;

import com.spring.guidely.entities.AppUser;
import com.spring.guidely.repository.UserRepository;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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

        // Load the credentials email template from classpath
        String template;
        try (InputStream is = new ClassPathResource("templates/user-acount-credentials.html").getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            template = reader.lines().collect(Collectors.joining("\n"));
        } catch (Exception e) {
            throw new RuntimeException("Failed to load email template", e);
        }

        // Replace placeholders with actual values
        String content = template
                .replace("{{userName}}", createdUser.getName())
                .replace("{{email}}", createdUser.getEmail())
                .replace("{{password}}", createdUser.getPassword());

        String subject = "Your Account Credentials";

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
