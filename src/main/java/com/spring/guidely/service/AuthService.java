package com.spring.guidely.service;

import com.spring.guidely.config.JwtService;
import com.spring.guidely.entities.AppUser;
import com.spring.guidely.entities.Role;
import com.spring.guidely.repository.AuthRepository;
import com.spring.guidely.repository.RoleRepository;
import com.spring.guidely.service.dto.AuthResponse;
import com.spring.guidely.web.error.AuthException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthRepository authRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtService jwtService;

    public AuthService(AuthRepository authRepository, RoleRepository roleRepository, JwtService jwtService) {
        this.authRepository = authRepository;
        this.roleRepository = roleRepository;
        this.jwtService = jwtService;
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

        String accessToken = jwtService.generateAccessToken(appUser.getEmail());
        String refreshToken = jwtService.generateRefreshToken(appUser.getEmail());

        // If storing refresh token in DB:
        //   RefreshToken entity = new RefreshToken();
        //   entity.setToken(refreshToken);
        //   entity.setUser(appUser);
        //   refreshTokenRepository.save(entity);

        return new AuthResponse(accessToken, refreshToken);
    }



    public AuthResponse refreshToken(String refreshToken) {
        // If storing tokens in DB:
        //   Check if refreshToken exists and is valid or not revoked.

        if (!jwtService.validateToken(refreshToken)) {
            throw new AuthException("Invalid refresh token!");
        }

        String email = jwtService.getEmailFromToken(refreshToken);

        // Generate a new Access Token
        String newAccessToken = jwtService.generateAccessToken(email);

        // Optionally generate a new Refresh Token as well
        String newRefreshToken = jwtService.generateRefreshToken(email);

        // If storing in DB, replace the old token with new one, etc.

        return new AuthResponse(newAccessToken, newRefreshToken);
    }
}
