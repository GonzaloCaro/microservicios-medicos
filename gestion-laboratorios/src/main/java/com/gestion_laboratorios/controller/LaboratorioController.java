package com.gestion_laboratorios.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.gestion_laboratorios.model.ResponseWrapper;
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

import com.gestion_laboratorios.DTO.LaboratorioDTO;
import com.gestion_laboratorios.exception.ResourceNotFoundException;
import com.gestion_laboratorios.hateoas.LaboratorioModelAssembler;
import com.gestion_laboratorios.mapper.LaboratorioMapper;
import com.gestion_laboratorios.model.Laboratorio;
import com.gestion_laboratorios.service.LaboratorioService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/laboratorios")
public class LaboratorioController {

    private final LaboratorioService laboratorioService;
    private final LaboratorioMapper laboratorioMapper;
    private final LaboratorioModelAssembler laboratorioModelAssembler;

    public LaboratorioController(LaboratorioService laboratorioService, LaboratorioMapper laboratorioMapper,
            LaboratorioModelAssembler laboratorioModelAssembler) {
        this.laboratorioService = laboratorioService;
        this.laboratorioMapper = laboratorioMapper;
        this.laboratorioModelAssembler = laboratorioModelAssembler;
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Laboratorio>>> getAllLaboratorios() {
        log.info("Recibiendo solicitud para obtener todos los laboratorios");
        List<Laboratorio> laboratorios = laboratorioService.getAllLaboratorios();

        if (laboratorios.isEmpty()) {
            log.warn("No se encontraron laboratorios");
            throw new ResourceNotFoundException("No se encontraron laboratorios");
        }
        log.debug("Controller: Se encontraron {} laboratorios", laboratorios.size());

        List<EntityModel<Laboratorio>> laboratorioModels = laboratorios.stream()
                .map(laboratorioModelAssembler::toModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                CollectionModel.of(laboratorioModels,
                        linkTo(methodOn(LaboratorioController.class).getAllLaboratorios()).withSelfRel()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Laboratorio>> getLaboratorioById(UUID id) {
        log.info("Recibiendo solicitud para obtener el laboratorio con ID: {}", id);
        Laboratorio laboratorio = laboratorioService.getLaboratorioById(id);

        if (laboratorio == null) {
            log.warn("Laboratorio no encontrado con ID: {}", id);
            throw new ResourceNotFoundException("Laboratorio no encontrado con ID: " + id);
        }
        log.debug("Controller: Laboratorio encontrado: {}", laboratorio);

        EntityModel<Laboratorio> laboratorioModel = laboratorioModelAssembler.toModel(laboratorio);
        return ResponseEntity.ok(laboratorioModel);
    }

    @PostMapping
    public ResponseEntity<EntityModel<Laboratorio>> createLaboratorio(@Valid @RequestBody LaboratorioDTO laboratorio) {
        log.info("Recibiendo solicitud para crear un nuevo laboratorio: {}", laboratorio);
        if (laboratorio == null) {
            log.warn("El laboratorio proporcionado es nulo");
            throw new IllegalArgumentException("El laboratorio no puede ser nulo");
        }
        try {
            Laboratorio createdLaboratorio = laboratorioService
                    .createLaboratorio(laboratorioMapper.toEntity(laboratorio));
            log.debug("Controller: Laboratorio creado: {}", createdLaboratorio);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(laboratorioModelAssembler.toModel(createdLaboratorio));
        } catch (Exception e) {
            throw e;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Laboratorio>> updateLaboratorio(@PathVariable UUID id,
            @RequestBody LaboratorioDTO laboratorioDTO) {
        log.info("Recibiendo solicitud para actualizar el laboratorio con ID: {}", id);
        if (id == null) {
            log.error("El ID del laboratorio no puede ser nulo");
            throw new IllegalArgumentException("El ID del laboratorio no puede ser nulo");
        }
        try {
            Laboratorio laboratorioDetails = laboratorioMapper.toEntity(laboratorioDTO);
            Laboratorio laboratorio = laboratorioService.updateLaboratorio(id, laboratorioDetails);
            EntityModel<Laboratorio> laboratorioModel = laboratorioModelAssembler.toModel(laboratorio);
            return ResponseEntity.ok(laboratorioModel);
        } catch (Exception e) {
            throw e;
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLaboratorio(@PathVariable UUID id) {
        log.info("Recibiendo solicitud para eliminar el laboratorio con ID: {}", id);
        if (id == null) {
            log.error("El ID del laboratorio no puede ser nulo");
            throw new IllegalArgumentException("El ID del laboratorio no puede ser nulo");
        }
        try {
            laboratorioService.deleteLaboratorio(id);
            log.debug("Controller: Laboratorio con ID {} eliminado", id);
            return ResponseEntity.ok(
                    new ResponseWrapper<>(
                            "Laboratorio eliminado exitosamente",
                            0,
                            null));
        } catch (Exception e) {
            throw e;
        }

    }

}
