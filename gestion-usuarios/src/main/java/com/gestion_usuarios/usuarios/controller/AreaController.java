package com.gestion_usuarios.usuarios.controller;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gestion_usuarios.usuarios.DTO.AreaDTO;
import com.gestion_usuarios.usuarios.hateoas.AreaModelAssembler;
import com.gestion_usuarios.usuarios.mapper.AreaMapper;
import com.gestion_usuarios.usuarios.model.Area;
import com.gestion_usuarios.usuarios.service.AreaService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/areas")
public class AreaController {

    private final AreaService areaService;
    private final AreaMapper areaMapper;
    private final AreaModelAssembler areaModelAssembler;

    public AreaController(AreaService areaService, AreaMapper areaMapper, AreaModelAssembler areaModelAssembler) {
        this.areaService = areaService;
        this.areaMapper = areaMapper;
        this.areaModelAssembler = areaModelAssembler;
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Area>>> getAllAreas() {
        log.info("Recibiendo solicitud para obtener todas las áreas");
        var areas = areaService.getAllAreas();
        if (areas.isEmpty()) {
            log.warn("No se encontraron áreas");
            return ResponseEntity.noContent().build();
        }
        log.debug("Controller: Se encontraron {} áreas", areas.size());
        List<EntityModel<Area>> areaModels = areas.stream()
                .map(areaModelAssembler::toModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                CollectionModel.of(areaModels,
                        linkTo(methodOn(AreaController.class).getAllAreas()).withSelfRel()));

    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Area>> getAreaById(@PathVariable UUID id) {
        log.info("Recibiendo solicitud para obtener el área con ID: {}", id);
        Area area = areaService.getAreaById(id);
        log.debug("Controller: Área encontrada: {}", area);
        return ResponseEntity.ok(areaModelAssembler.toModel(area));
    }

    @PostMapping
    public ResponseEntity<EntityModel<Area>> createArea(@Valid @RequestBody AreaDTO areaDTO) {
        log.info("Recibiendo solicitud para crear un área");

        if (areaDTO == null) {
            log.error("El área no puede ser nula");
            throw new IllegalArgumentException("El área no puede ser nula");
        }
        try {
            Area createdArea = areaService.createArea(areaMapper.toEntity(areaDTO));
            log.debug("Controller: Área creada: {}", createdArea);
            return ResponseEntity.status(HttpStatus.CREATED).body(areaModelAssembler.toModel(createdArea));
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArea(@PathVariable UUID id) {
        log.info("Recibiendo solicitud para eliminar el área con ID: {}", id);
        if (id == null) {
            log.error("El ID del área no puede ser nulo");
            throw new IllegalArgumentException("El ID del área no puede ser nulo");
        }
        try {
            areaService.deleteArea(id);
            log.debug("Controller: Área eliminada con ID: {}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

}
