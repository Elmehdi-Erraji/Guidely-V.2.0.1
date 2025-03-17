package com.spring.guidely.service.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class AuthResponse {

    private String id;
    private String role;
    private String accessToken;
    private String refreshToken;

    public AuthResponse() {}

    public AuthResponse(String id , String role,String accessToken, String refreshToken) {
        this.id = id;
        this.role = role;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

}
