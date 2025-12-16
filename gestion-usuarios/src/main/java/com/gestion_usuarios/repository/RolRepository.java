package com.gestion_usuarios.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gestion_usuarios.model.Rol;

@Repository
public interface RolRepository extends JpaRepository<Rol, UUID> {

}
