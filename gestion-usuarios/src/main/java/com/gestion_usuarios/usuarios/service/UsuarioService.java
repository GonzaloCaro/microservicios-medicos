package com.gestion_usuarios.usuarios.service;

import com.gestion_usuarios.usuarios.model.Usuario;
import com.gestion_usuarios.usuarios.repository.UsuarioRepository;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<Usuario> getAllUsuarios() {
        log.info("Obteniendo todos los usuarios");
        return usuarioRepository.findAll();
    }

    @Transactional
    public Usuario createUsuario(Usuario usuario) {
        log.info("Creando un nuevo usuario: {}", usuario);
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario getUsuarioById(UUID id) {
        log.info("Obteniendo el usuario con ID: {}", id);
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
    }

    @Transactional
    public Usuario updateUsuario(UUID id, Usuario usuarioDetails) {
        log.info("Actualizando el usuario con ID: {}", id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        usuario.setNombre(usuarioDetails.getNombre());
        usuario.setEmail(usuarioDetails.getEmail());
        usuario.setContrasena(usuarioDetails.getContrasena());

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void deleteUsuario(UUID id) {
        log.info("Eliminando el usuario con ID: {}", id);
        usuarioRepository.deleteById(id);
    }

}
