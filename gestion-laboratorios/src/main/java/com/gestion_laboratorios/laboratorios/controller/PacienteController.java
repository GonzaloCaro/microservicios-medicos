package com.gestion_laboratorios.laboratorios.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gestion_laboratorios.laboratorios.DTO.PacienteDTO;
import com.gestion_laboratorios.laboratorios.exception.ResourceNotFoundException;
import com.gestion_laboratorios.laboratorios.hateoas.PacienteModelAssembler;
import com.gestion_laboratorios.laboratorios.mapper.PacienteMapper;
import com.gestion_laboratorios.laboratorios.model.Analisis;
import com.gestion_laboratorios.laboratorios.model.Paciente;
import com.gestion_laboratorios.laboratorios.service.PacienteService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/paciente")
public class PacienteController {

    private final PacienteService pacienteService;
    private final PacienteMapper pacienteMapper;
    private final PacienteModelAssembler pacienteModelAssembler;

    public PacienteController(PacienteService pacienteService, PacienteMapper pacienteMapper,
            PacienteModelAssembler pacienteModelAssembler) {
        this.pacienteService = pacienteService;
        this.pacienteMapper = pacienteMapper;
        this.pacienteModelAssembler = pacienteModelAssembler;
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Paciente>>> getAllPacientes() {
        log.info("Recibiendo solicitud para obtener todos los pacientes");
        List<Paciente> pacientes = pacienteService.getAllPacientes();
        if (pacientes.isEmpty()) {
            log.warn("No se encontraron pacientes");
            throw new ResourceNotFoundException("No se encontraron pacientes");
        }

        List<EntityModel<Paciente>> pacienteModels = pacientes.stream()
                .map(pacienteModelAssembler::toModel)
                .collect(Collectors.toList());
        CollectionModel<EntityModel<Paciente>> collectionModel = CollectionModel.of(pacienteModels,
                linkTo(methodOn(PacienteController.class).getAllPacientes()).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Paciente>> getPacienteById(@PathVariable UUID id) {
        log.info("Recibiendo solicitud para obtener el paciente con ID: {}", id);
        Paciente paciente = pacienteService.getPacienteById(id);
        EntityModel<Paciente> pacienteModel = pacienteModelAssembler.toModel(paciente);
        return ResponseEntity.ok(pacienteModel);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Paciente>> updatePaciente(@PathVariable UUID id,
            @RequestBody PacienteDTO paciente) {
        log.info("Recibiendo solicitud para actualizar el paciente con ID: {}", id);
        if (paciente == null) {
            log.error("El DTO de paciente es nulo");
            throw new IllegalArgumentException("El DTO de paciente no puede ser nulo");
        }
        try {
            Paciente pacienteToUpdate = pacienteMapper.toEntity(paciente);
            Paciente updatedPaciente = pacienteService.updatePaciente(id, pacienteToUpdate);
            EntityModel<Paciente> pacienteModel = pacienteModelAssembler.toModel(updatedPaciente);
            return ResponseEntity.ok(pacienteModel);
        } catch (Exception e) {
            log.error("Error al actualizar el paciente con ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Error al actualizar el paciente", e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePaciente(@PathVariable UUID id) {
        log.info("Recibiendo solicitud para eliminar el paciente con ID: {}", id);
        try {
            pacienteService.deletePaciente(id);
            log.info("Paciente con ID {} eliminado exitosamente", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error al eliminar el paciente con ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Error al eliminar el paciente", e);
        }
    }
}
