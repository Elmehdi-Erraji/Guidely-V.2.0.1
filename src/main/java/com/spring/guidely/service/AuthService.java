package com.spring.guidely.service;

import com.spring.guidely.entities.AppUser;
import com.spring.guidely.service.dto.AuthResponse;
import com.spring.guidely.web.vm.auth.LoginRequestVM;
import com.spring.guidely.web.vm.auth.RegisterRequestVM;
import com.spring.guidely.web.vm.auth.RegisterResponseVM;

public interface AuthService {
    RegisterResponseVM register(RegisterRequestVM registerRequest);
    AuthResponse login(LoginRequestVM loginRequest);
    AuthResponse refreshToken(String refreshToken);
    void requestPasswordReset(String email);
    void resetPassword(String token, String newPassword);
}
