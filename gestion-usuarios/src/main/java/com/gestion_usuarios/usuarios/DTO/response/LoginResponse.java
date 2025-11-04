package com.gestion_usuarios.usuarios.DTO.response;

import java.util.UUID;

public class LoginResponse {
    private String accessToken;
    private UUID userId;
    private String userName;

    public LoginResponse(String accessToken, UUID userId, String userName) {
        this.accessToken = accessToken;
        this.userId = userId;
        this.userName = userName;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

}