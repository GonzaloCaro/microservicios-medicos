package com.gestion_usuarios.usuarios.model;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "role_users")
@Data
public class RoleUser {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private Usuario usuario;

    @OneToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Rol rol;

    @OneToOne
    @JoinColumn(name = "area_id", nullable = false)
    private Area area;
}
