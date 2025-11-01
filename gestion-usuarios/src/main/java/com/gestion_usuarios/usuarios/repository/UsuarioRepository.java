package com.gestion_usuarios.usuarios.repository;

import com.gestion_usuarios.usuarios.model.Usuario;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

}
