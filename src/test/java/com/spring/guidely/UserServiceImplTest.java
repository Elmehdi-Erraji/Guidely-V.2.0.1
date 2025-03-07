package com.spring.guidely;

import com.spring.guidely.entities.AppUser;
import com.spring.guidely.repository.UserRepository;
import com.spring.guidely.service.EmailService;
import com.spring.guidely.service.Impl.UserServiceImpl;
import com.spring.guidely.service.UserService;
import com.spring.guidely.web.error.UserAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Helper: simulate reading email template successfully by placing a dummy file in test resources.
    // Ensure you have a file at src/test/resources/templates/user-acount-credentials.html with some dummy content.

    @Test
    void saveUser_Success() throws Exception {
        // Prepare a sample AppUser to be saved
        AppUser user = new AppUser();
        user.setId(UUID.randomUUID());
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("secret123");

        // Stub: No user exists with that email
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(user);

        // Execute
        AppUser createdUser = userService.save(user);

        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(userRepository, times(1)).save(user);
        verify(rabbitTemplate, times(1)).convertAndSend(anyString(), anyString(), any(Map.class));

        assertNotNull(createdUser);
        assertEquals("test@example.com", createdUser.getEmail());
    }

    @Test
    void saveUser_DuplicateEmail_ThrowsException() {
        AppUser user = new AppUser();
        user.setName("Test User");
        user.setEmail("duplicate@example.com");
        user.setPassword("secret123");

        when(userRepository.findByEmail("duplicate@example.com")).thenReturn(Optional.of(new AppUser()));

        UserAlreadyExistsException ex = assertThrows(UserAlreadyExistsException.class,
                () -> userService.save(user));
        assertEquals("User with email duplicate@example.com already exists.", ex.getMessage());
        verify(userRepository, times(1)).findByEmail("duplicate@example.com");
        verify(userRepository, never()).save(any());
        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), any(Map.class));
    }

    @Test
    void updateUser_Success() {
        UUID userId = UUID.randomUUID();
        AppUser existingUser = new AppUser();
        existingUser.setId(userId);
        existingUser.setName("Old Name");
        existingUser.setEmail("old@example.com");
        existingUser.setPassword("oldpass");

        AppUser updateUser = new AppUser();
        updateUser.setId(userId);
        updateUser.setName("New Name");
        updateUser.setEmail("new@example.com");
        updateUser.setPassword("newpass");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        AppUser result = userService.update(updateUser);

        assertNotNull(result);
        assertEquals("New Name", result.getName());
        assertEquals("new@example.com", result.getEmail());
        assertEquals("newpass", result.getPassword());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).findByEmail("new@example.com");
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void updateUser_DuplicateEmail_ThrowsException() {
        UUID userId = UUID.randomUUID();
        AppUser existingUser = new AppUser();
        existingUser.setId(userId);
        existingUser.setName("Old Name");
        existingUser.setEmail("old@example.com");
        existingUser.setPassword("oldpass");

        AppUser updateUser = new AppUser();
        updateUser.setId(userId);
        updateUser.setName("New Name");
        updateUser.setEmail("conflict@example.com");
        updateUser.setPassword("newpass");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        AppUser anotherUser = new AppUser();
        anotherUser.setId(UUID.randomUUID());
        anotherUser.setEmail("conflict@example.com");
        when(userRepository.findByEmail("conflict@example.com")).thenReturn(Optional.of(anotherUser));

        UserAlreadyExistsException ex = assertThrows(UserAlreadyExistsException.class,
                () -> userService.update(updateUser));
        assertEquals("User with email conflict@example.com already exists.", ex.getMessage());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).findByEmail("conflict@example.com");
        verify(userRepository, never()).save(any());
    }

    @Test
    void getUserById_Found() {
        UUID userId = UUID.randomUUID();
        AppUser user = new AppUser();
        user.setId(userId);
        user.setName("Test User");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Optional<AppUser> result = userService.getUserById(userId);
        assertTrue(result.isPresent());
        assertEquals(userId, result.get().getId());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getUserById_NotFound() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Optional<AppUser> result = userService.getUserById(userId);
        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getAllUsers_Pageable() {
        AppUser user = new AppUser();
        user.setId(UUID.randomUUID());
        user.setName("Test User");

        Pageable pageable = PageRequest.of(0, 10);
        Page<AppUser> page = new PageImpl<>(List.of(user), pageable, 1);
        when(userRepository.findAll(pageable)).thenReturn(page);

        Page<AppUser> result = userService.getAllUsers(pageable);
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(userRepository, times(1)).findAll(pageable);
    }

    @Test
    void deleteUser_Success() {
        UUID userId = UUID.randomUUID();
        AppUser user = new AppUser();
        user.setId(userId);
        user.setName("Test User");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).deleteById(userId);

        assertDoesNotThrow(() -> userService.delete(userId));
        verify(userRepository, times(1)).deleteById(userId);
    }
}
