package com.gestion_laboratorios.laboratorios.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gestion_laboratorios.laboratorios.model.Asignacion;

@Repository
public interface AsignacionRepository extends JpaRepository<Asignacion, UUID> {

    @EntityGraph(attributePaths = { "laboratorio", "analisis", "paciente" })
    List<Asignacion> findAll();

    @EntityGraph(attributePaths = { "laboratorio", "analisis", "paciente" })
    Optional<Asignacion> findById(UUID id);

    @EntityGraph(attributePaths = { "laboratorio", "analisis", "paciente" })
    Optional<Asignacion> findByUsuarioId(UUID usuarioId);
}
