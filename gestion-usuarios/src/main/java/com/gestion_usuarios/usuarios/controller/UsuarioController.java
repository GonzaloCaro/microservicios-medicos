package com.gestion_usuarios.usuarios.controller;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.gestion_usuarios.usuarios.DTO.UsuarioDTO;
import com.gestion_usuarios.usuarios.exception.ResourceNotFoundException;
import com.gestion_usuarios.usuarios.model.Usuario;
import com.gestion_usuarios.usuarios.service.UsuarioService;

import java.util.List;
import java.util.UUID;

// 
import com.gestion_usuarios.usuarios.hateoas.UsuarioModelAssembler;
import com.gestion_usuarios.usuarios.mapper.UsuarioMapper;

import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import java.util.stream.Collectors;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Slf4j
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper;
    private final UsuarioModelAssembler usuarioModelAssembler;

    public UsuarioController(UsuarioService usuarioService, UsuarioMapper usuarioMapper,
            UsuarioModelAssembler usuarioModelAssembler) {
        this.usuarioService = usuarioService;
        this.usuarioMapper = usuarioMapper;
        this.usuarioModelAssembler = usuarioModelAssembler;
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Usuario>>> getAllUsuarios() {
        log.info("Recibiendo solicitud para obtener todos los usuarios");
        List<Usuario> usuarios = usuarioService.getAllUsuarios();

        if (usuarios.isEmpty()) {
            log.warn("No se encontraron usuarios");
            throw new ResourceNotFoundException("No se encontraron usuarios");
        }
        log.debug("Controller: Se encontraron {} usuarios", usuarios.size());

        List<EntityModel<Usuario>> usuarioModels = usuarios.stream()
                .map(usuarioModelAssembler::toModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                CollectionModel.of(usuarioModels,
                        linkTo(methodOn(UsuarioController.class).getAllUsuarios()).withSelfRel()));

    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Usuario>> getUsuarioById(@PathVariable UUID id) {
        log.info("Recibiendo solicitud para obtener el usuario con ID: {}", id);
        if (id == null) {
            log.error("El ID del usuario no puede ser nulo");
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        }
        try {
            Usuario usuario = usuarioService.getUsuarioById(id);
            log.debug("Controller: Usuario encontrado: {}", usuario);
            return ResponseEntity.ok(usuarioModelAssembler.toModel(usuario));
        } catch (Exception e) {
            throw e;
        }

    }

    @PostMapping
    public ResponseEntity<EntityModel<Usuario>> createUsuario(@Valid @RequestBody UsuarioDTO usuarioDTO) {
        log.debug("usuarioDTO: {}", usuarioDTO);
        log.debug("Controller: Creando nuevo usuario");
        if (usuarioDTO == null) {
            log.error("El usuario no puede ser nulo");
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }
        try {
            Usuario createdUsuario = usuarioService.createUsuario(usuarioMapper.toEntity(usuarioDTO));
            log.debug("Controller: Usuario creado: {}", createdUsuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(usuarioModelAssembler.toModel(createdUsuario));
        } catch (Exception e) {
            throw e;
        }

    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Usuario>> updateUsuario(@PathVariable UUID id,
            @Valid @RequestBody UsuarioDTO usuarioDTO) {
        log.info("Recibiendo solicitud para actualizar el usuario con ID: {}", id);
        if (id == null) {
            log.error("El ID del usuario no puede ser nulo");
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        }
        try {
            Usuario usuarioDetails = usuarioMapper.toEntity(usuarioDTO);

            Usuario updatedUsuario = usuarioService.updateUsuario(id, usuarioDetails);
            log.debug("Controller: Usuario actualizado: {}", updatedUsuario);
            return ResponseEntity.ok(usuarioModelAssembler.toModel(updatedUsuario));
        } catch (Exception e) {
            throw e;
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable UUID id) {
        log.info("Recibiendo solicitud para eliminar el usuario con ID: {}", id);
        if (id == null) {
            log.error("El ID del usuario no puede ser nulo");
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        }
        try {
            usuarioService.deleteUsuario(id);
            log.debug("Controller: Usuario con ID {} eliminado", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            throw e;
        }

    }

}
