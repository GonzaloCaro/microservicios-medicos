package com.gestion_usuarios.DTO.response;

import java.util.UUID;

public class LoginResponse {
    private String accessToken;
    private UUID userId;
    private String userName;
    private String nombre;
    private String apellido;
    private String email;
    private UUID roleId;
    private String roleNombre;
    private UUID areaId;
    private String areaNombre;

    public LoginResponse(String accessToken, UUID userId, String userName, String nombre, String apellido, String email,
            UUID roleId,
            String roleNombre, UUID areaId, String areaNombre) {
        this.accessToken = accessToken;
        this.userId = userId;
        this.userName = userName;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.roleId = roleId;
        this.roleNombre = roleNombre;
        this.areaId = areaId;
        this.areaNombre = areaNombre;
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

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getEmail() {
        return email;
    }

    public UUID getRoleId() {
        return roleId;
    }

    public String getRoleNombre() {
        return roleNombre;
    }

    public UUID getAreaId() {
        return areaId;
    }

    public String getAreaNombre() {
        return areaNombre;
    }
}