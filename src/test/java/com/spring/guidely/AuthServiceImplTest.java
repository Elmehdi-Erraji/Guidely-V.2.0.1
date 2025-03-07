package com.spring.guidely;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.spring.guidely.config.JwtService;
import com.spring.guidely.entities.AppUser;
import com.spring.guidely.entities.PasswordResetToken;
import com.spring.guidely.entities.Role;
import com.spring.guidely.repository.AuthRepository;
import com.spring.guidely.repository.PasswordResetTokenRepository;
import com.spring.guidely.repository.RoleRepository;
import com.spring.guidely.service.EmailService;
import com.spring.guidely.service.Impl.AuthServiceImpl;
import com.spring.guidely.service.dto.AuthResponse;
import com.spring.guidely.web.error.AuthException;
import com.spring.guidely.web.vm.auth.LoginRequestVM;
import com.spring.guidely.web.vm.auth.RegisterRequestVM;
import com.spring.guidely.web.vm.auth.RegisterResponseVM;
import java.time.LocalDateTime;
import java.util.*;

import com.spring.guidely.web.vm.mapers.AuthVMMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

class AuthServiceImplTest {

    @Mock
    private AuthRepository authRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private EmailService emailService;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private AuthVMMapper authVMMapper;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private AuthServiceImpl authService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        authService = new AuthServiceImpl(authRepository, roleRepository, jwtService,
                emailService, passwordResetTokenRepository, rabbitTemplate, authVMMapper);
    }

    // ------------------- register() Tests -------------------

    @Test
    void register_Success() {

        RegisterRequestVM requestVM = new RegisterRequestVM();
        requestVM.setName("John Doe");
        requestVM.setEmail("john@example.com");
        requestVM.setPassword("password123");

        AppUser userEntity = new AppUser();
        userEntity.setEmail(requestVM.getEmail());
        userEntity.setName(requestVM.getName());
        when(authVMMapper.toEntity(requestVM)).thenReturn(userEntity);

        Role userRole = new Role();
        userRole.setName("USER");
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));

        AppUser savedUser = new AppUser();
        UUID userId = UUID.randomUUID();
        savedUser.setId(userId);
        savedUser.setName(requestVM.getName());
        savedUser.setEmail(requestVM.getEmail());
        savedUser.setRole(userRole);
        savedUser.setPassword(passwordEncoder.encode(requestVM.getPassword()));
        when(authRepository.save(any(AppUser.class))).thenReturn(savedUser);

        RegisterResponseVM responseVM = new RegisterResponseVM();
        responseVM.setId(userId.toString());
        responseVM.setName(savedUser.getName());
        responseVM.setEmail(savedUser.getEmail());
        responseVM.setRole("USER");
        when(authVMMapper.toResponse(savedUser)).thenReturn(responseVM);

        RegisterResponseVM result = authService.register(requestVM);

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        assertEquals("john@example.com", result.getEmail());
        assertEquals("USER", result.getRole());
    }

    @Test
    void register_EmailAlreadyExists_ThrowsException() {
        RegisterRequestVM requestVM = new RegisterRequestVM();
        requestVM.setName("Jane Doe");
        requestVM.setEmail("jane@example.com");
        requestVM.setPassword("password123");

        when(authRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(new AppUser()));

        AuthException exception = assertThrows(AuthException.class, () -> {
            authService.register(requestVM);
        });
        assertEquals("Email already exists", exception.getMessage());
    }

    // ------------------- login() Tests -------------------

    @Test
    void login_Success() {
        LoginRequestVM loginVM = new LoginRequestVM();
        loginVM.setEmail("john@example.com");
        loginVM.setPassword("password123");

        AppUser user = new AppUser();
        user.setEmail(loginVM.getEmail());
        user.setPassword(passwordEncoder.encode("password123"));

        when(authRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken(user)).thenReturn("access-token");
        when(jwtService.generateRefreshToken(user)).thenReturn("refresh-token");

        AuthResponse response = authService.login(loginVM);

        assertNotNull(response);
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
    }

    @Test
    void login_EmailNotFound_ThrowsException() {
        LoginRequestVM loginVM = new LoginRequestVM();
        loginVM.setEmail("notfound@example.com");
        loginVM.setPassword("password123");

        when(authRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        AuthException exception = assertThrows(AuthException.class, () -> {
            authService.login(loginVM);
        });
        assertEquals("Email not found", exception.getMessage());
    }

    @Test
    void login_InvalidPassword_ThrowsException() {
        LoginRequestVM loginVM = new LoginRequestVM();
        loginVM.setEmail("john@example.com");
        loginVM.setPassword("wrongpassword");

        AppUser user = new AppUser();
        user.setEmail(loginVM.getEmail());
        user.setPassword(passwordEncoder.encode("password123"));

        when(authRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));

        AuthException exception = assertThrows(AuthException.class, () -> {
            authService.login(loginVM);
        });
        assertEquals("Invalid credentials!", exception.getMessage());
    }

    // ------------------- refreshToken() Tests -------------------

    @Test
    void refreshToken_Success() {
        String refreshToken = "valid-refresh-token";
        String email = "john@example.com";

        when(jwtService.validateToken(refreshToken)).thenReturn(true);
        when(jwtService.getEmailFromToken(refreshToken)).thenReturn(email);

        AppUser user = new AppUser();
        user.setEmail(email);
        when(authRepository.findByEmail(email)).thenReturn(Optional.of(user));

        when(jwtService.generateAccessToken(user)).thenReturn("new-access-token");
        when(jwtService.generateRefreshToken(user)).thenReturn("new-refresh-token");

        AuthResponse response = authService.refreshToken(refreshToken);

        assertNotNull(response);
        assertEquals("new-access-token", response.getAccessToken());
        assertEquals("new-refresh-token", response.getRefreshToken());
    }

    @Test
    void refreshToken_InvalidToken_ThrowsException() {
        String refreshToken = "invalid-token";
        when(jwtService.validateToken(refreshToken)).thenReturn(false);

        AuthException exception = assertThrows(AuthException.class, () -> {
            authService.refreshToken(refreshToken);
        });
        assertEquals("Invalid refresh token!", exception.getMessage());
    }

    // ------------------- requestPasswordReset() Tests -------------------

    @Test
    void requestPasswordReset_Success() {
        String email = "john@example.com";
        AppUser user = new AppUser();
        user.setEmail(email);
        user.setName("John Doe");
        when(authRepository.findByEmail(email)).thenReturn(Optional.of(user));

        assertDoesNotThrow(() -> authService.requestPasswordReset(email));
        verify(passwordResetTokenRepository, times(1)).save(any(PasswordResetToken.class));
        verify(rabbitTemplate, times(1)).convertAndSend(anyString(), anyString(), any(Map.class));
    }

    @Test
    void requestPasswordReset_EmailNotFound_ThrowsException() {
        String email = "notfound@example.com";
        when(authRepository.findByEmail(email)).thenReturn(Optional.empty());

        AuthException exception = assertThrows(AuthException.class, () -> {
            authService.requestPasswordReset(email);
        });
        assertEquals("Email not found", exception.getMessage());
    }


    @Test
    void resetPassword_Success() {
        String token = "reset-token";
        String newPassword = "newpassword123";
        AppUser user = new AppUser();
        user.setPassword(passwordEncoder.encode("oldpassword"));

        PasswordResetToken prt = new PasswordResetToken();
        prt.setToken(token);
        prt.setExpiration(LocalDateTime.now().plusMinutes(60));
        prt.setUser(user);

        when(passwordResetTokenRepository.findByToken(token)).thenReturn(Optional.of(prt));

        assertDoesNotThrow(() -> authService.resetPassword(token, newPassword));
        verify(authRepository, times(1)).save(user);
        verify(passwordResetTokenRepository, times(1)).delete(prt);
    }

    @Test
    void resetPassword_TokenNotFound_ThrowsException() {
        String token = "nonexistent-token";
        when(passwordResetTokenRepository.findByToken(token)).thenReturn(Optional.empty());

        AuthException exception = assertThrows(AuthException.class, () -> {
            authService.resetPassword(token, "newpassword");
        });
        assertEquals("Invalid password reset token", exception.getMessage());
    }

    @Test
    void resetPassword_TokenExpired_ThrowsException() {
        String token = "expired-token";
        AppUser user = new AppUser();
        user.setPassword(passwordEncoder.encode("oldpassword"));

        PasswordResetToken prt = new PasswordResetToken();
        prt.setToken(token);
        prt.setExpiration(LocalDateTime.now().minusMinutes(1));
        prt.setUser(user);

        when(passwordResetTokenRepository.findByToken(token)).thenReturn(Optional.of(prt));

        AuthException exception = assertThrows(AuthException.class, () -> {
            authService.resetPassword(token, "newpassword");
        });
        assertEquals("Token has expired", exception.getMessage());
    }
}
