package com.gestion_laboratorios.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gestion_laboratorios.DTO.AsignacionDTO;
import com.gestion_laboratorios.model.Asignacion;
import com.gestion_laboratorios.model.Paciente;
import com.gestion_laboratorios.repository.AsignacionRepository;
import com.gestion_laboratorios.repository.PacienteRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AsignacionService {

    private final AsignacionRepository asignacionRepository;
    private final PacienteRepository pacienteRepository;

    public AsignacionService(AsignacionRepository asignacionRepository, PacienteRepository pacienteRepository) {
        this.asignacionRepository = asignacionRepository;
        this.pacienteRepository = pacienteRepository;
    }

    // El @EntityGraph del repositorio se encarga de traer las relaciones aquí
    public List<Asignacion> getAllAsignaciones() {
        log.info("Obteniendo todas las asignaciones");
        return asignacionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Asignacion getAsignacionById(UUID id) {
        log.info("Obteniendo la asignacion con ID: {}", id);
        return asignacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asignacion no encontrada con ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Asignacion> getAsignacionesByUserId(UUID userId) {
        log.info("Obteniendo asignaciones para el usuario ID: {}", userId);
        return asignacionRepository.findByUsuarioId(userId);
    }

    @Transactional
    public Asignacion crearAsignacion(AsignacionDTO dto) {
        log.info("Creando una nueva asignacion para RUT: {}", dto.getRut());

        Paciente paciente;

        if (dto.getPacienteId() != null) {
            log.info("Buscando paciente existente por ID: {}", dto.getPacienteId());
            paciente = pacienteRepository.findById(dto.getPacienteId())
                    .orElseThrow(() -> new RuntimeException("Paciente no encontrado con ID provisto"));
        } else {
            Optional<Paciente> existente = pacienteRepository.findByRut(dto.getRut());

            if (existente.isPresent()) {
                log.info("Paciente encontrado por RUT");
                paciente = existente.get();
            } else {
                log.info("Creando nuevo paciente");
                paciente = new Paciente();
                paciente.setRut(dto.getRut());
                paciente.setDv(dto.getDv());
                paciente.setEdad(dto.getEdad());
                paciente.setNombrePaciente(dto.getNombrePaciente());
                paciente.setApellidoPaciente(dto.getApellidoPaciente());
                paciente.setTelefono(dto.getTelefono());

                paciente = pacienteRepository.save(paciente);
            }
        }

        Asignacion asignacion = new Asignacion();
        asignacion.setPaciente(paciente);
        asignacion.setLaboratorioId(dto.getLaboratorioId());
        asignacion.setUsuarioId(dto.getUsuarioId());
        asignacion.setAnalisisId(dto.getAnalisisId());
        asignacion.setPacienteId(paciente.getId());
        asignacion.setFechaAsignacion(LocalDateTime.now());
        asignacion.setDetalle(dto.getDetalle());

        return asignacionRepository.save(asignacion);
    }

    @Transactional
    public Asignacion updateAsignacion(UUID id, AsignacionDTO dto) {
        log.info("Actualizando la asignacion con ID: {}", id);

        Asignacion asignacion = asignacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asignacion no encontrada con ID: " + id));

        if (dto.getLaboratorioId() != null) {
            asignacion.setLaboratorioId(dto.getLaboratorioId());
        }

        if (dto.getAnalisisId() != null) {
            asignacion.setAnalisisId(dto.getAnalisisId());
        }

        if (dto.getDetalle() != null) {
            asignacion.setDetalle(dto.getDetalle());
        }

        return asignacionRepository.save(asignacion);
    }

    @Transactional
    public void deleteAsignacion(UUID id) {
        log.info("Eliminando la asignacion con ID: {}", id);
        if (!asignacionRepository.existsById(id)) {
            throw new RuntimeException("No se puede eliminar, asignacion no encontrada");
        }
        asignacionRepository.deleteById(id);
    }
}