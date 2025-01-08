package com.spring.guidely.web.rest;

import com.spring.guidely.entities.AppUser;
import com.spring.guidely.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity<AppUser> createUser(@RequestBody AppUser user) {
        AppUser myUser = userService.save(user);
        return new ResponseEntity<>(myUser, HttpStatus.CREATED);
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<AppUser> findById(@PathVariable UUID id) {
        Optional<AppUser> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @GetMapping("/findAll")
    public ResponseEntity<Page<AppUser>> findAll(Pageable pageable) {
        Page<AppUser> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        Optional<AppUser> user = userService.getUserById(id);
        if (user.isPresent()) {
            userService.delete(id);
            return new ResponseEntity<>(HttpStatus.OK);
        }else {
            throw new IllegalArgumentException("User not found");
        }
    }

}
