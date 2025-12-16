package com.gestion_laboratorios.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gestion_laboratorios.model.Analisis;

@Repository
public interface AnalisisRepository extends JpaRepository<Analisis, UUID> {

}
