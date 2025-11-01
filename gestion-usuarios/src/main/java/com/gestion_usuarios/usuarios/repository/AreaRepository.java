package com.gestion_usuarios.usuarios.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gestion_usuarios.usuarios.model.Area;

@Repository
public interface AreaRepository extends JpaRepository<Area, UUID> {

}
