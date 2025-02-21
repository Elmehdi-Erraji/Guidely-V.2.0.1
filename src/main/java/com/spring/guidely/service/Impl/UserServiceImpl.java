package com.spring.guidely.service.Impl;

import com.spring.guidely.config.RabbitMQConfig;
import com.spring.guidely.entities.AppUser;
import com.spring.guidely.repository.UserRepository;
import com.spring.guidely.service.EmailService;
import com.spring.guidely.service.UserService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
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
        AppUser createdUser = userRepository.save(user);

        // Load the credentials email template from classpath (e.g. src/main/resources/templates/user-acount-credentials.html)
        String template;
        try (InputStream is = new ClassPathResource("templates/user-acount-credentials.html").getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            template = reader.lines().collect(Collectors.joining("\n"));
        } catch (Exception e) {
            throw new RuntimeException("Failed to load email template", e);
        }

        String content = template
                .replace("{{userName}}", createdUser.getName())
                .replace("{{email}}", createdUser.getEmail())
                .replace("{{password}}", createdUser.getPassword());

        String subject = "Your Account Credentials";

        // Create a Map with email details
        Map<String, String> emailData = new HashMap<>();
        emailData.put("to", createdUser.getEmail());
        emailData.put("subject", subject);
        emailData.put("html", content);

        // Publish the email data to RabbitMQ for asynchronous processing
        rabbitTemplate.convertAndSend(RabbitMQConfig.EMAIL_EXCHANGE, RabbitMQConfig.EMAIL_ROUTING_KEY, emailData);

        return createdUser;
    }

    @Override
    public AppUser update(AppUser user) {
        return userRepository.save(user);
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
