package com.gestion_usuarios.usuarios.DTO;

import java.util.UUID;

public class AreaDTO {
    private UUID id;
    private String nombre;

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
