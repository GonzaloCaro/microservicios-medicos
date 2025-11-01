package com.gestion_laboratorios.laboratorios.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gestion_laboratorios.laboratorios.model.Asignacion;

@Repository
public interface AsignacionRepository extends JpaRepository<Asignacion, UUID> {

}
