package com.spring.guidely.service.dto;

public class AuthResponse {

    private String role;
    private String accessToken;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    private String refreshToken;

    public AuthResponse() {}

    public AuthResponse(String role,String accessToken, String refreshToken) {
        this.role = role;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    // getters, setters
    public String getAccessToken() {
        return accessToken;
    }
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    public String getRefreshToken() {
        return refreshToken;
    }
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
