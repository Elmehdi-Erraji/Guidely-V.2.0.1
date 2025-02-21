package com.spring.guidely.service;

import com.spring.guidely.entities.AppUser;
import com.spring.guidely.service.dto.AuthResponse;

public interface AuthService {
    AppUser register(AppUser appUser);
    AuthResponse login(String email, String rawPassword);
    AuthResponse refreshToken(String refreshToken);
    void requestPasswordReset(String email);
    void resetPassword(String token, String newPassword);
}
