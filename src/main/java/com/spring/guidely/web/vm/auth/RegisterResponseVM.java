package com.spring.guidely.web.vm.auth;

import lombok.Data;

@Data
public class RegisterResponseVM {
    private String id;
    private String name;
    private String email;
    private String role;
}
