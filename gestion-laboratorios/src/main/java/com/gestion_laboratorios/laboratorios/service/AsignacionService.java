package com.gestion_laboratorios.laboratorios.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gestion_laboratorios.laboratorios.model.Asignacion;
import com.gestion_laboratorios.laboratorios.repository.AsignacionRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AsignacionService {

    private final AsignacionRepository asignacionRepository;

    public AsignacionService(AsignacionRepository asignacionRepository) {
        this.asignacionRepository = asignacionRepository;
    }

    public List<Asignacion> getAllAsignaciones() {
        log.info("Obteniendo todas las asignaciones");
        return asignacionRepository.findAll();
    }

    @Transactional
    public Asignacion getAsignacionById(UUID id) {
        log.info("Obteniendo la asignacion con ID: {}", id);
        return asignacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asignacion no encontrada con ID: " + id));
    }

    @Transactional
    public Asignacion createAsignacion(Asignacion asignacion) {
        log.info("Creando una nueva asignacion: {}", asignacion);
        return asignacionRepository.save(asignacion);
    }

    @Transactional
    public Asignacion updateAsignacion(UUID id, Asignacion asignacionDetails) {
        log.info("Actualizando la asignacion con ID: {}", id);
        Asignacion asignacion = asignacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asignacion no encontrada con ID: " + id));

        asignacion.setLaboratorioId(asignacionDetails.getLaboratorioId());
        asignacion.setUsuarioId(asignacionDetails.getUsuarioId());
        asignacion.setFechaAsignacion(asignacionDetails.getFechaAsignacion());

        return asignacionRepository.save(asignacion);
    }

    @Transactional
    public void deleteAsignacion(UUID id) {
        log.info("Eliminando la asignacion con ID: {}", id);
        asignacionRepository.deleteById(id);
    }

}
