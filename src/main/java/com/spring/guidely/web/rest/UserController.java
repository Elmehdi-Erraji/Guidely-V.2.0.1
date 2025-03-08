package com.spring.guidely.web.rest;

import com.spring.guidely.entities.AppUser;
import com.spring.guidely.entities.Department;
import com.spring.guidely.entities.Role;
import com.spring.guidely.repository.DepartmentRepository;
import com.spring.guidely.repository.RoleRepository;
import com.spring.guidely.service.UserService;
import com.spring.guidely.web.vm.mapers.UserVMMapper;
import com.spring.guidely.web.vm.users.UserCreateRequestVM;
import com.spring.guidely.web.vm.users.UserResponseVM;
import com.spring.guidely.web.vm.users.UserUpdateRequestVM;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserVMMapper userVMMapper;
    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;

    public UserController(UserService userService,
                          UserVMMapper userVMMapper,
                          RoleRepository roleRepository,
                          DepartmentRepository departmentRepository) {
        this.userService = userService;
        this.userVMMapper = userVMMapper;
        this.roleRepository = roleRepository;
        this.departmentRepository = departmentRepository;
    }

    @PostMapping("/create")
    public ResponseEntity<UserResponseVM> createUser(@Valid @RequestBody UserCreateRequestVM createRequest) {
        AppUser userEntity = userVMMapper.toEntity(createRequest);
        Role role = roleRepository.findById(createRequest.getRoleId())
                .orElseThrow(() -> new IllegalArgumentException("Role not found with id: " + createRequest.getRoleId()));
        Department department = departmentRepository.findById(createRequest.getDepartmentId())
                .orElseThrow(() -> new IllegalArgumentException("Department not found with id: " + createRequest.getDepartmentId()));
        userEntity.setRole(role);
        userEntity.setDepartment(department);
        AppUser savedUser = userService.save(userEntity);
        UserResponseVM response = userVMMapper.toResponse(savedUser);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<UserResponseVM> updateUser(@PathVariable UUID id,
                                                     @Valid @RequestBody UserUpdateRequestVM updateRequest) {
        AppUser existingUser = userService.getUserById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        userVMMapper.updateEntity(updateRequest, existingUser);
        Role role = roleRepository.findById(updateRequest.getRoleId())
                .orElseThrow(() -> new IllegalArgumentException("Role not found with id: " + updateRequest.getRoleId()));
        Department department = departmentRepository.findById(updateRequest.getDepartmentId())
                .orElseThrow(() -> new IllegalArgumentException("Department not found with id: " + updateRequest.getDepartmentId()));
        existingUser.setRole(role);
        existingUser.setDepartment(department);
        AppUser updatedUser = userService.update(existingUser);
        UserResponseVM response = userVMMapper.toResponse(updatedUser);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<UserResponseVM> findById(@PathVariable UUID id) {
        AppUser user = userService.getUserById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        UserResponseVM response = userVMMapper.toResponse(user);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/findAll")
    public ResponseEntity<Page<UserResponseVM>> findAll(Pageable pageable) {
        Page<AppUser> users = userService.getAllUsers(pageable);
        Page<UserResponseVM> response = users.map(userVMMapper::toResponse);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        if (userService.getUserById(id).isEmpty()) {
            throw new IllegalArgumentException("User not found with id: " + id);
        }
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
