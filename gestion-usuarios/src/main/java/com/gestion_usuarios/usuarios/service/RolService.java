package com.gestion_usuarios.usuarios.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gestion_usuarios.usuarios.model.Rol;
import com.gestion_usuarios.usuarios.repository.RolRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RolService {

    private final RolRepository rolRepository;

    public RolService(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    public List<Rol> getAllRoles() {
        log.info("Obteniendo todos los roles");
        return rolRepository.findAll();
    }

    @Transactional
    public Rol createRol(Rol rol) {
        log.info("Creando un nuevo rol: {}", rol);
        return rolRepository.save(rol);
    }

    @Transactional
    public Rol getRolById(UUID id) {
        log.info("Obteniendo el rol con ID: {}", id);
        return rolRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + id));
    }

    @Transactional
    public Rol updateRol(UUID id, Rol rolDetails) {
        log.info("Actualizando el rol con ID: {}", id);
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + id));

        rol.setNombre(rolDetails.getNombre());

        return rolRepository.save(rol);
    }

    @Transactional
    public void deleteRol(UUID id) {
        log.info("Eliminando el rol con ID: {}", id);
        rolRepository.deleteById(id);
    }
}
