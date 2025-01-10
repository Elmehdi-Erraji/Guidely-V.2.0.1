package com.spring.guidely.service;

import com.spring.guidely.config.JwtService;
import com.spring.guidely.entities.AppUser;
import com.spring.guidely.entities.PasswordResetToken;
import com.spring.guidely.entities.Role;
import com.spring.guidely.repository.AuthRepository;
import com.spring.guidely.repository.PasswordResetTokenRepository;
import com.spring.guidely.repository.RoleRepository;
import com.spring.guidely.service.dto.AuthResponse;
import com.spring.guidely.web.error.AuthException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class AuthService {

    private final AuthRepository authRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtService jwtService;
    private final EmailService emailService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;



    private final int resetTokenExpirationMinutes = 60;

    public AuthService(AuthRepository authRepository, RoleRepository roleRepository, JwtService jwtService, EmailService emailService, PasswordResetTokenRepository passwordResetTokenRepository) {
        this.authRepository = authRepository;
        this.roleRepository = roleRepository;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

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

    public AuthResponse login(String email, String rawPassword) {
        AppUser appUser = authRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException("Email not found"));

        if (!passwordEncoder.matches(rawPassword, appUser.getPassword())) {
            throw new AuthException("Invalid credentials!");
        }

        String accessToken = jwtService.generateAccessToken(appUser);
        String refreshToken = jwtService.generateRefreshToken(appUser);

        // If storing refresh token in DB:
        //   RefreshToken entity = new RefreshToken();
        //   entity.setToken(refreshToken);
        //   entity.setUser(appUser);
        //   refreshTokenRepository.save(entity);

        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse refreshToken(String refreshToken) {
        // If storing tokens in DB, check if refreshToken exists and is valid/not revoked.

        if (!jwtService.validateToken(refreshToken)) {
            throw new AuthException("Invalid refresh token!");
        }

        // We need the user to generate a new token with user-based claims.
        String email = jwtService.getEmailFromToken(refreshToken);

        // Fetch the AppUser from the DB so we can call generateAccessToken(AppUser)
        AppUser appUser = authRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException("User not found for refresh token"));

        // Generate a new Access Token
        String newAccessToken = jwtService.generateAccessToken(appUser);

        // Optionally generate a new Refresh Token as well
        String newRefreshToken = jwtService.generateRefreshToken(appUser);

        // If storing in DB, replace the old token with new one, etc.

        return new AuthResponse(newAccessToken, newRefreshToken);
    }


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


    public void requestPasswordReset(String email) {
        AppUser user = authRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException("Email not found"));

        // Generate a reset token
        String token = UUID.randomUUID().toString();

        // Set expiration (e.g., 60 minutes from now)
        LocalDateTime expiration = LocalDateTime.now().plusMinutes(resetTokenExpirationMinutes);
        String formattedExpiration = expiration.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        // Save the token in DB
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setExpiration(expiration);
        resetToken.setUser(user);
        passwordResetTokenRepository.save(resetToken);

        // Load and render the email template
        String template = loadEmailTemplate("templates/password-reset-email.html");
        String emailContent = renderEmailTemplate(template, user.getName(), token, formattedExpiration);

        // Send the email
        String subject = "Password Reset Request";
        emailService.sendEmail(user.getEmail(), subject, emailContent);

        // Log the token for debugging
        System.out.println("Password Reset Token: " + token);
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new AuthException("Invalid password reset token"));

        // Check if token is expired
        if (resetToken.isExpired()||resetToken.getExpiration().isBefore(LocalDateTime.now())) {
            throw new AuthException("Token has expired");
        }

        // Get the user
        AppUser user = resetToken.getUser();

        // Update the user's password
        user.setPassword(passwordEncoder.encode(newPassword));
        authRepository.save(user);

        // Token has been used; remove it so it can't be reused
        passwordResetTokenRepository.delete(resetToken);
    }

}
