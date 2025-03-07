package com.spring.guidely.web.vm.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class UserUpdateRequestVM {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    private String password;

    @NotNull(message = "Role ID is required")
    private UUID roleId;

    @NotNull(message = "Department ID is required")
    private UUID departmentId;
}
