package com.spring.guidely.service;

import com.spring.guidely.entities.AppUser;
import com.spring.guidely.entities.Role;
import com.spring.guidely.repository.AuthRepository;
import com.spring.guidely.repository.RoleRepository;
import com.spring.guidely.web.error.AuthException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthRepository authRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(AuthRepository authRepository, RoleRepository roleRepository) {
        this.authRepository = authRepository;
        this.roleRepository = roleRepository;
    }

    public AppUser register(AppUser appUser) {

        if (authRepository.findByEmail(appUser.getEmail()).isPresent()) {
            throw new AuthException("Email already exists");
        }

        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new AuthException("Default role USER not found"));

        appUser.setRole(userRole);

        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));

        System.out.println(appUser);

        return authRepository.save(appUser);
    }

    public String login(String email, String password) {
        AppUser appUser = authRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException("Email not found"));

        if (!passwordEncoder.matches(password, appUser.getPassword())) {
            throw new AuthException("Invalid credentials!");
        }

        return "Logged in successfully";
    }
}
