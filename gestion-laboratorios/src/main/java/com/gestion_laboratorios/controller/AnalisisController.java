package com.gestion_laboratorios.controller;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gestion_laboratorios.DTO.AnalisisDTO;
import com.gestion_laboratorios.exception.ResourceNotFoundException;
import com.gestion_laboratorios.hateoas.AnalisisModelAssembler;
import com.gestion_laboratorios.mapper.AnalisisMapper;
import com.gestion_laboratorios.model.Analisis;
import com.gestion_laboratorios.service.AnalisisService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/analisis")
public class AnalisisController {

    private final AnalisisService analisisService;
    private final AnalisisMapper analisisMapper;
    private final AnalisisModelAssembler analisisModelAssembler;

    public AnalisisController(AnalisisService analisisService, AnalisisMapper analisisMapper,
            AnalisisModelAssembler analisisModelAssembler) {
        this.analisisService = analisisService;
        this.analisisMapper = analisisMapper;
        this.analisisModelAssembler = analisisModelAssembler;
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Analisis>>> getAllAnalisis() {
        log.info("Recibiendo solicitud para obtener todos los análisis");
        List<Analisis> analisisList = analisisService.getAllAnalisis();

        if (analisisList.isEmpty()) {
            log.warn("No se encontraron análisis");
            throw new ResourceNotFoundException("No se encontraron análisis");
        }

        List<EntityModel<Analisis>> analisisModels = analisisList.stream()
                .map(analisisModelAssembler::toModel)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<Analisis>> collectionModel = CollectionModel.of(analisisModels,
                linkTo(methodOn(AnalisisController.class).getAllAnalisis()).withSelfRel());

        log.info("Enviando {} análisis encontrados", analisisModels.size());
        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Analisis>> getAnalisisById(@PathVariable UUID id) {
        log.info("Recibiendo solicitud para obtener el análisis con ID: {}", id);
        Analisis analisis = analisisService.getAnalisisById(id);

        EntityModel<Analisis> analisisModel = analisisModelAssembler.toModel(analisis);

        log.info("Enviando análisis con ID: {}", id);
        return ResponseEntity.ok(analisisModel);
    }

    @PostMapping
    public ResponseEntity<EntityModel<Analisis>> createAnalisis(@Valid @RequestBody AnalisisDTO dto) {
        log.info("Recibiendo solicitud para crear un nuevo análisis");
        if (dto == null) {
            log.error("El DTO de análisis es nulo");
            throw new IllegalArgumentException("El DTO de análisis no puede ser nulo");
        }
        try {
            Analisis newAnalisis = analisisService.createAnalisis(dto);
            EntityModel<Analisis> analisisModel = analisisModelAssembler.toModel(newAnalisis);
            return ResponseEntity.ok(analisisModel);
        } catch (Exception e) {
            log.error("Error al crear el análisis: {}", e.getMessage());
            throw new RuntimeException("Error al crear el análisis", e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Analisis>> updateAnalisis(@PathVariable UUID id,
            @Valid @RequestBody AnalisisDTO dto) {
        log.info("Recibiendo solicitud para actualizar el análisis con ID: {}", id);
        if (dto == null) {
            log.error("El DTO de análisis es nulo");
            throw new IllegalArgumentException("El DTO de análisis no puede ser nulo");
        }
        try {
            Analisis analisisToUpdate = analisisMapper.toEntity(dto);
            Analisis updatedAnalisis = analisisService.updateAnalisis(id, analisisToUpdate);
            EntityModel<Analisis> analisisModel = analisisModelAssembler.toModel(updatedAnalisis);
            return ResponseEntity.ok(analisisModel);
        } catch (Exception e) {
            log.error("Error al actualizar el análisis con ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Error al actualizar el análisis", e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnalisis(@PathVariable UUID id) {
        log.info("Recibiendo solicitud para eliminar el análisis con ID: {}", id);
        if (id == null) {
            log.error("El ID del análisis no puede ser nulo");
            throw new IllegalArgumentException("El ID del análisis no puede ser nulo");
        }
        try {
            analisisService.deleteAnalisis(id);
            log.info("Análisis con ID {} eliminado exitosamente", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error al eliminar el análisis con ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Error al eliminar el análisis", e);
        }
    }

}
