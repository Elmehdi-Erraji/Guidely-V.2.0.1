package com.spring.guidely.service.Impl;

import com.spring.guidely.config.RabbitMQConfig;
import com.spring.guidely.entities.AppUser;
import com.spring.guidely.repository.UserRepository;
import com.spring.guidely.service.EmailService;
import com.spring.guidely.service.UserService;
import com.spring.guidely.web.error.UserAlreadyExistsException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final RabbitTemplate rabbitTemplate;

    public UserServiceImpl(UserRepository userRepository, EmailService emailService, RabbitTemplate rabbitTemplate) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public AppUser save(AppUser user) {
        // Check if the user email already exists
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("User with email " + user.getEmail() + " already exists.");
        }
        AppUser createdUser = userRepository.save(user);

        // Load the credentials email template from classpath (e.g., src/main/resources/templates/user-acount-credentials.html)
        String template;
        try (InputStream is = new ClassPathResource("templates/user-acount-credentials.html").getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            template = reader.lines().collect(Collectors.joining("\n"));
        } catch (Exception e) {
            throw new RuntimeException("Failed to load email template", e);
        }

        String content = template
                .replace("{{userName}}", createdUser.getName())
                .replace("{{email}}", createdUser.getEmail())
                .replace("{{password}}", createdUser.getPassword());

        String subject = "Your Account Credentials";

        Map<String, String> emailData = new HashMap<>();
        emailData.put("to", createdUser.getEmail());
        emailData.put("subject", subject);
        emailData.put("html", content);

        // Publish email details for asynchronous processing via RabbitMQ
        rabbitTemplate.convertAndSend(RabbitMQConfig.EMAIL_EXCHANGE, RabbitMQConfig.EMAIL_ROUTING_KEY, emailData);

        return createdUser;
    }

    @Override
    public AppUser update(AppUser user) {
        // Fetch the existing user
        AppUser existingUser = getUserById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + user.getId()));

        // If the email is updated, check if the new email already exists (and is not the same as current user's email)
        if (!existingUser.getEmail().equals(user.getEmail())) {
            Optional<AppUser> userByEmail = userRepository.findByEmail(user.getEmail());
            if (userByEmail.isPresent() && !userByEmail.get().getId().equals(existingUser.getId())) {
                throw new UserAlreadyExistsException("User with email " + user.getEmail() + " already exists.");
            }
        }

        // Update other fields as needed (for simplicity, we update name, email, password)
        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            existingUser.setPassword(user.getPassword());
        }
        // Note: updating role and department associations should be handled before calling update (e.g., via service layer logic)

        return userRepository.save(existingUser);
    }

    @Override
    public Optional<AppUser> getUserById(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public Page<AppUser> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public void delete(UUID id) {
        userRepository.deleteById(id);
    }
}
