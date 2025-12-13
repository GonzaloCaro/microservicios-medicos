package com.gestion_laboratorios.laboratorios.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gestion_laboratorios.laboratorios.DTO.AsignacionDTO;
import com.gestion_laboratorios.laboratorios.model.Asignacion;
import com.gestion_laboratorios.laboratorios.model.Paciente;
import com.gestion_laboratorios.laboratorios.repository.AsignacionRepository;
import com.gestion_laboratorios.laboratorios.repository.PacienteRepository;

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
    public List<Asignacion> getAsignacionesByUserId(UUID userId) {
        log.info("Obteniendo la asignacion con medico ID: {}", userId);
        return asignacionRepository.findByUsuarioId(userId);
    }

    @Transactional
    public Asignacion crearAsignacion(AsignacionDTO dto) {
        log.info("Creando una nueva asignacion", dto);

        Paciente paciente;

        // Si llega pacienteId, buscarlo
        if (dto.getPacienteId() != null) {
            log.info("Buscando paciente con ID: {}", dto.getPacienteId());
            paciente = pacienteRepository.findById(dto.getPacienteId())
                    .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
        }
        // Si no llega ID, crear un nuevo paciente
        else {
            Optional<Paciente> existente = pacienteRepository.findByRut(dto.getRut());

            if (existente.isPresent()) {
                paciente = existente.get();
            } else {
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

        // 3️⃣ Crear la asignación
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
