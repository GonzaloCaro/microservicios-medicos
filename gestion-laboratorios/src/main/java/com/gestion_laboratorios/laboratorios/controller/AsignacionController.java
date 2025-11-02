package com.gestion_laboratorios.laboratorios.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gestion_laboratorios.laboratorios.DTO.AsignacionDTO;
import com.gestion_laboratorios.laboratorios.DTO.LaboratorioDTO;
import com.gestion_laboratorios.laboratorios.exception.ResourceNotFoundException;
import com.gestion_laboratorios.laboratorios.hateoas.AsignacionModelAssembler;
import com.gestion_laboratorios.laboratorios.mapper.AsignacionMapper;
import com.gestion_laboratorios.laboratorios.model.Asignacion;
import com.gestion_laboratorios.laboratorios.model.Laboratorio;
import com.gestion_laboratorios.laboratorios.model.ResponseWrapper;
import com.gestion_laboratorios.laboratorios.service.AsignacionService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/asignacion_lab")
public class AsignacionController {

    private final AsignacionService asignacionService;
    private final AsignacionMapper asignacionMapper;
    private final AsignacionModelAssembler asignacionModelAssembler;

    public AsignacionController(AsignacionService asignacionService, AsignacionMapper asignacionMapper,
            AsignacionModelAssembler asignacionModelAssembler) {
        this.asignacionService = asignacionService;
        this.asignacionMapper = asignacionMapper;
        this.asignacionModelAssembler = asignacionModelAssembler;
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Asignacion>>> getAllAsignaciones() {
        log.info("Recibiendo solicitud para obtener todas las asignaciones");
        List<Asignacion> asignaciones = asignacionService.getAllAsignaciones();

        if (asignaciones.isEmpty()) {
            log.warn("No se encontraron asignaciones");
            throw new ResourceNotFoundException("No se encontraron asignaciones");
        }
        log.debug("Controller: Se encontraron {} asignaciones", asignaciones.size());

        List<EntityModel<Asignacion>> asignacionModels = asignaciones.stream()
                .map(asignacionModelAssembler::toModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                CollectionModel.of(asignacionModels,
                        linkTo(methodOn(AsignacionController.class).getAllAsignaciones()).withSelfRel()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Asignacion>> getAsignacionById(@PathVariable UUID id) {
        log.info("Recibiendo solicitud para obtener la asignacion con ID: {}", id);
        Asignacion asignacion = asignacionService.getAsignacionById(id);

        if (asignacion == null) {
            log.warn("Asignacion no encontrada con ID: {}", id);
            throw new ResourceNotFoundException("Asignacion no encontrada con ID: " + id);
        }
        log.debug("Controller: Asignacion encontrada: {}", asignacion);

        EntityModel<Asignacion> asignacionModel = asignacionModelAssembler.toModel(asignacion);
        return ResponseEntity.ok(asignacionModel);
    }

    @PostMapping
    public ResponseEntity<EntityModel<Asignacion>> createAsignacion(@Valid @RequestBody AsignacionDTO asignacionDTO) {
        log.info("Recibiendo solicitud para crear una nueva asignación: {}", asignacionDTO);

        if (asignacionDTO == null) {
            log.warn("La asignación proporcionada es nula");
            throw new IllegalArgumentException("La asignación no puede ser nula");
        }

        try {
            // El servicio se encarga de validar si el paciente existe o crear uno nuevo
            Asignacion createdAsignacion = asignacionService.crearAsignacion(asignacionDTO);

            log.debug("Controller: Asignación creada exitosamente con ID {}", createdAsignacion.getId());

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(asignacionModelAssembler.toModel(createdAsignacion));

        } catch (Exception e) {
            log.error("Error al crear la asignación: {}", e.getMessage(), e);
            throw e; // o podrías envolverlo en una excepción custom tipo AsignacionCreationException
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Asignacion>> updateAsignacion(@PathVariable UUID id,
            @RequestBody AsignacionDTO asignacionDTO) {
        log.info("Recibiendo solicitud para actualizar la asignacion con ID: {}", id);
        if (id == null) {
            log.error("El ID de la asignacion no puede ser nulo");
            throw new IllegalArgumentException("El ID de la asignacion no puede ser nulo");
        }
        try {
            Asignacion asignacionDetails = asignacionMapper.toEntity(asignacionDTO);
            Asignacion asignacion = asignacionService.updateAsignacion(id, asignacionDetails);
            EntityModel<Asignacion> asignacionModel = asignacionModelAssembler.toModel(asignacion);
            return ResponseEntity.ok(asignacionModel);
        } catch (Exception e) {
            throw e;
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAsignacion(@PathVariable UUID id) {
        log.info("Recibiendo solicitud para eliminar la asignacion con ID: {}", id);
        if (id == null) {
            log.error("El ID de la asignacion no puede ser nulo");
            throw new IllegalArgumentException("El ID de la asignacion no puede ser nulo");
        }
        try {
            asignacionService.deleteAsignacion(id);
            log.debug("Controller: Asignacion con ID {} eliminada", id);
            return ResponseEntity.ok(
                    new ResponseWrapper<>(
                            "Asignacion eliminada exitosamente",
                            0,
                            null));
        } catch (Exception e) {
            throw e;
        }

    }

}
