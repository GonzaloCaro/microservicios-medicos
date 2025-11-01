package com.gestion_usuarios.usuarios.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "NOMBRE", nullable = false)
    private String nombre;

    @Column(name = "APELLIDO", nullable = false)
    private String apellido;

    @Column(name = "USER_NAME", unique = true, nullable = false)
    private String userName;

    @Column(name = "EMAIL", unique = true, nullable = false)
    private String email;

    @Column(name = "CONTRASENA", nullable = false)
    private String contrasena;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoleUser> roles = new ArrayList<>();

    // Getters y Setters
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public UUID getRoleId() {
        if (roles != null && !roles.isEmpty()) {
            return roles.get(0).getRol().getId();
        }
        return null;
    }

    public void setRoleId(UUID roleId) {
        if (roles == null) {
            roles = new ArrayList<>();
        }

        // si no hay registro, crea uno vacío
        if (roles.isEmpty()) {
            roles.add(new RoleUser());
        }

        RoleUser relation = roles.get(0);
        if (relation.getRol() == null) {
            Rol rol = new Rol();
            rol.setId(roleId);
            relation.setRol(rol);
        } else {
            relation.getRol().setId(roleId);
        }

        relation.setUsuario(this);
    }

    public UUID getAreaId() {
        if (roles != null && !roles.isEmpty()) {
            return roles.get(0).getArea().getId();
        }
        return null;
    }

    public void setAreaId(UUID areaId) {
        if (roles == null) {
            roles = new ArrayList<>();
        }

        if (roles.isEmpty()) {
            roles.add(new RoleUser());
        }

        RoleUser relation = roles.get(0);
        if (relation.getArea() == null) {
            Area area = new Area();
            area.setId(areaId);
            relation.setArea(area);
        } else {
            relation.getArea().setId(areaId);
        }

        relation.setUsuario(this);
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", contrasena='" + contrasena + '\'' +
                '}';
    }
}
