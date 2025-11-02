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

import com.gestion_usuarios.usuarios.hateoas.RolModelAssembler;
import com.gestion_usuarios.usuarios.mapper.RolMapper;
import com.gestion_usuarios.usuarios.model.ResponseWrapper;
import com.gestion_usuarios.usuarios.model.Rol;
import com.gestion_usuarios.usuarios.service.RolService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/roles")
public class RolController {

    private final RolService rolService;
    private final RolMapper rolMapper;
    private final RolModelAssembler rolModelAssembler;

    public RolController(RolService rolService, RolMapper rolMapper, RolModelAssembler rolModelAssembler) {
        this.rolService = rolService;
        this.rolMapper = rolMapper;
        this.rolModelAssembler = rolModelAssembler;
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Rol>>> getAllRoles() {
        log.info("Recibiendo solicitud para obtener todos los roles");
        var roles = rolService.getAllRoles();

        if (roles.isEmpty()) {
            log.warn("No se encontraron roles");
            return ResponseEntity.noContent().build();
        }
        log.debug("Controller: Se encontraron {} roles", roles.size());

        List<EntityModel<Rol>> rolModels = roles.stream()
                .map(rolModelAssembler::toModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                CollectionModel.of(rolModels,
                        linkTo(methodOn(RolController.class).getAllRoles()).withSelfRel()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Rol>> getRolById(@PathVariable UUID id) {
        log.info("Recibiendo solicitud para obtener el rol con ID: {}", id);
        if (id == null) {
            log.error("El ID del rol no puede ser nulo");
            throw new IllegalArgumentException("El ID del rol no puede ser nulo");
        }
        try {
            Rol rol = rolService.getRolById(id);
            log.debug("Controller: Rol encontrado: {}", rol);
            return ResponseEntity.ok(rolModelAssembler.toModel(rol));
        } catch (Exception e) {
            throw e;
        }

    }

    @PostMapping
    public ResponseEntity<EntityModel<Rol>> createRol(@Valid @RequestBody Rol newRol) {
        log.debug("rolDTO: {}", newRol);
        log.debug("Controller: Creando nuevo rol");
        if (newRol == null) {
            log.error("El rol no puede ser nulo");
            throw new IllegalArgumentException("El rol no puede ser nulo");
        }
        try {
            Rol createdRol = rolService.createRol(newRol);
            log.debug("Controller: Rol creado: {}", createdRol);
            return ResponseEntity.status(HttpStatus.CREATED).body(rolModelAssembler.toModel(createdRol));
        } catch (Exception e) {
            throw e;
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRol(@PathVariable UUID id) {
        log.info("Recibiendo solicitud para eliminar el rol con ID: {}", id);
        if (id == null) {
            log.error("El ID del rol no puede ser nulo");
            throw new IllegalArgumentException("El ID del rol no puede ser nulo");
        }
        try {
            rolService.deleteRol(id);
            log.debug("Controller: Rol eliminado con ID: {}", id);
            return ResponseEntity.ok(
                    new ResponseWrapper<>(
                            "Rol eliminado exitosamente",
                            0,
                            null));
        } catch (Exception e) {
            throw e;
        }
    }

}
