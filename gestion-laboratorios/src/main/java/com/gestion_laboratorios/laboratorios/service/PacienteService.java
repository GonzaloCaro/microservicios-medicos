package com.gestion_laboratorios.laboratorios.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gestion_laboratorios.laboratorios.DTO.PacienteDTO;
import com.gestion_laboratorios.laboratorios.model.Paciente;
import com.gestion_laboratorios.laboratorios.repository.PacienteRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PacienteService {

    private final PacienteRepository pacienteRepository;

    public PacienteService(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    public List<Paciente> getAllPacientes() {
        log.info("Obteniendo todos los pacientes");
        return pacienteRepository.findAll();
    }

    @Transactional
    public Paciente createPaciente(PacienteDTO pacienteDTO) {
        log.info("Creando un nuevo paciente");
        Paciente paciente = new Paciente();
        paciente.setRut(pacienteDTO.getRut());
        paciente.setDv(pacienteDTO.getDv());
        paciente.setNombrePaciente(pacienteDTO.getNombrePaciente());
        paciente.setApellidoPaciente(pacienteDTO.getApellidoPaciente());
        paciente.setEdad(pacienteDTO.getEdad());
        paciente.setTelefono(pacienteDTO.getTelefono());
        return pacienteRepository.save(paciente);
    }

    @Transactional
    public Paciente getPacienteById(UUID id) {
        log.info("Obteniendo el paciente con ID: {}", id);
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con ID: " + id));
    }

    @Transactional
    public Paciente updatePaciente(UUID id, Paciente paciente) {
        log.info("Actualizando el paciente con ID: {}", id);
        Paciente existingPaciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con ID: " + id));
        existingPaciente.setRut(paciente.getRut());
        existingPaciente.setDv(paciente.getDv());
        existingPaciente.setNombrePaciente(paciente.getNombrePaciente());
        existingPaciente.setApellidoPaciente(paciente.getApellidoPaciente());
        existingPaciente.setEdad(paciente.getEdad());
        existingPaciente.setTelefono(paciente.getTelefono());
        return pacienteRepository.save(existingPaciente);
    }

    @Transactional
    public void deletePaciente(UUID id) {
        log.info("Eliminando el paciente con ID: {}", id);
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con ID: " + id));
        pacienteRepository.delete(paciente);
    }
}
