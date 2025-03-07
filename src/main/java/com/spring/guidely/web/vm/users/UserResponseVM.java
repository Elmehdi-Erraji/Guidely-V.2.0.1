package com.spring.guidely.web.vm.users;

import lombok.Data;

import java.util.UUID;


@Data
public class UserResponseVM {
    private UUID id;
    private String name;
    private String email;
    private String roleName;
    private String departmentName;
}
