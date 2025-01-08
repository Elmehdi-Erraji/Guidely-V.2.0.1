package com.spring.guidely.web.rest;

import com.spring.guidely.entities.AppUser;
import com.spring.guidely.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public String login(@RequestBody AppUser appUser) {
        return authService.login(appUser.getEmail(), appUser.getPassword());
    }

    @PostMapping("/register")
    public ResponseEntity<AppUser> register(@RequestBody AppUser appUser) {
        return ResponseEntity.ok(authService.register(appUser));
    }
}
