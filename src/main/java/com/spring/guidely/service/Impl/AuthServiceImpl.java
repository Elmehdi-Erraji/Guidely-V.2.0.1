package com.spring.guidely.service.Impl;

import com.spring.guidely.config.JwtService;
import com.spring.guidely.config.RabbitMQConfig;
import com.spring.guidely.entities.AppUser;
import com.spring.guidely.entities.PasswordResetToken;
import com.spring.guidely.entities.Role;
import com.spring.guidely.repository.AuthRepository;
import com.spring.guidely.repository.PasswordResetTokenRepository;
import com.spring.guidely.repository.RoleRepository;
import com.spring.guidely.service.AuthService;
import com.spring.guidely.service.EmailService;
import com.spring.guidely.service.dto.AuthResponse;
import com.spring.guidely.web.error.AuthException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthRepository authRepository;
    private final RoleRepository roleRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final RabbitTemplate rabbitTemplate;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final int resetTokenExpirationMinutes = 60;

    public AuthServiceImpl(AuthRepository authRepository,
                           RoleRepository roleRepository,
                           JwtService jwtService,
                           EmailService emailService,
                           PasswordResetTokenRepository passwordResetTokenRepository,
                           RabbitTemplate rabbitTemplate) {
        this.authRepository = authRepository;
        this.roleRepository = roleRepository;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public AppUser register(AppUser appUser) {
        if (authRepository.findByEmail(appUser.getEmail()).isPresent()) {
            throw new AuthException("Email already exists");
        }

        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new AuthException("Default role USER not found"));

        appUser.setRole(userRole);
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));

        return authRepository.save(appUser);
    }

    @Override
    public AuthResponse login(String email, String rawPassword) {
        AppUser appUser = authRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException("Email not found"));

        if (!passwordEncoder.matches(rawPassword, appUser.getPassword())) {
            throw new AuthException("Invalid credentials!");
        }

        String accessToken = jwtService.generateAccessToken(appUser);
        String refreshToken = jwtService.generateRefreshToken(appUser);

        // Optionally save refresh token in DB if needed

        return new AuthResponse(accessToken, refreshToken);
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtService.validateToken(refreshToken)) {
            throw new AuthException("Invalid refresh token!");
        }

        String email = jwtService.getEmailFromToken(refreshToken);

        AppUser appUser = authRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException("User not found for refresh token"));

        String newAccessToken = jwtService.generateAccessToken(appUser);
        String newRefreshToken = jwtService.generateRefreshToken(appUser);

        return new AuthResponse(newAccessToken, newRefreshToken);
    }

    @Override
    public void requestPasswordReset(String email) {
        AppUser user = authRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException("Email not found"));

        String token = UUID.randomUUID().toString();
        LocalDateTime expiration = LocalDateTime.now().plusMinutes(resetTokenExpirationMinutes);
        String formattedExpiration = expiration.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setExpiration(expiration);
        resetToken.setUser(user);
        passwordResetTokenRepository.save(resetToken);

        String template = loadEmailTemplate("templates/password-reset-email.html");
        String emailContent = renderEmailTemplate(template, user.getName(), token, formattedExpiration);
        String subject = "Password Reset Request";

        Map<String, String> emailData = new HashMap<>();
        emailData.put("to", user.getEmail());
        emailData.put("subject", subject);
        emailData.put("html", emailContent);

        // Publish email details to RabbitMQ for asynchronous sending
        rabbitTemplate.convertAndSend(RabbitMQConfig.EMAIL_EXCHANGE, RabbitMQConfig.EMAIL_ROUTING_KEY, emailData);

        System.out.println("Password Reset Token: " + token);
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new AuthException("Invalid password reset token"));

        if (resetToken.isExpired() || resetToken.getExpiration().isBefore(LocalDateTime.now())) {
            throw new AuthException("Token has expired");
        }

        AppUser user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        authRepository.save(user);
        passwordResetTokenRepository.delete(resetToken);
    }

    // Private helper methods

    private String loadEmailTemplate(String templatePath) {
        try {
            InputStream inputStream = new ClassPathResource(templatePath).getInputStream();
            byte[] bytes = FileCopyUtils.copyToByteArray(inputStream);
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load email template", e);
        }
    }

    private String renderEmailTemplate(String template, String userName, String token, String expiration) {
        return template
                .replace("{{userName}}", userName)
                .replace("{{token}}", token)
                .replace("{{expiration}}", expiration);
    }
}
