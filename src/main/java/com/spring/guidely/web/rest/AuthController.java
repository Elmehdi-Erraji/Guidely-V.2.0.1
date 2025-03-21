package com.spring.guidely.web.rest;

import com.spring.guidely.entities.AppUser;
import com.spring.guidely.service.AuthService;
import com.spring.guidely.service.dto.AuthResponse;
import com.spring.guidely.web.vm.auth.LoginRequestVM;
import com.spring.guidely.web.vm.auth.RegisterRequestVM;
import com.spring.guidely.web.vm.auth.RegisterResponseVM;
import com.spring.guidely.web.vm.mapers.UserVMMapper;
import com.spring.guidely.web.vm.users.UserResponseVM;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserVMMapper userVMMapper;


    public AuthController(AuthService authService, UserVMMapper userVMMapper) {
        this.authService = authService;
        this.userVMMapper = userVMMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseVM> register(
            @Valid @RequestBody RegisterRequestVM registerRequest) {
        RegisterResponseVM response = authService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequestVM loginRequest) {
        AuthResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestParam String refreshToken) {
        AuthResponse response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgetPassword")
    public ResponseEntity<Void> requestPasswordReset(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        authService.requestPasswordReset(email);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<Void> resetPassword(
            @RequestParam String token,
            @RequestParam String newPassword) {
        authService.resetPassword(token, newPassword);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/profile")
    public ResponseEntity<UserResponseVM> getProfile(Authentication authentication) {
        AppUser user = (AppUser) authentication.getPrincipal();
        UserResponseVM response = userVMMapper.toResponse(user);
        return ResponseEntity.ok(response);
    }

}
