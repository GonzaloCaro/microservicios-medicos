package com.gestion_usuarios.usuarios.service;

import com.gestion_usuarios.usuarios.encoder.PasswordEncoder;
import com.gestion_usuarios.usuarios.model.Usuario;
import com.gestion_usuarios.usuarios.repository.UsuarioRepository;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = new PasswordEncoder();
    }

    public List<Usuario> getAllUsuarios() {
        log.info("Obteniendo todos los usuarios");
        return usuarioRepository.findAll();
    }

    @Transactional
    public Usuario createUsuario(Usuario usuario) {
        log.info("Creando un nuevo usuario: {}", usuario);
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
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

        // Actualizar solo si vienen datos
        if (usuarioDetails.getNombre() != null) {
            usuario.setNombre(usuarioDetails.getNombre());
        }

        if (usuarioDetails.getApellido() != null) {
            usuario.setApellido(usuarioDetails.getApellido());
        }

        if (usuarioDetails.getUserName() != null) {
            usuario.setUserName(usuarioDetails.getUserName());
        }

        if (usuarioDetails.getEmail() != null) {
            usuario.setEmail(usuarioDetails.getEmail());
        }
        if (usuarioDetails.getContrasena() != null) {
            usuario.setContrasena(passwordEncoder.encode(usuarioDetails.getContrasena()));
        }

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void deleteUsuario(UUID id) {
        log.info("Eliminando el usuario con ID: {}", id);
        usuarioRepository.deleteById(id);
    }

    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Usuario u = usuarioRepository.findByUserName(userName)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (u.getRole() != null && u.getRole().getRol() != null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + u.getRole().getRol().getNombre().toUpperCase()));
        }
        return new org.springframework.security.core.userdetails.User(
                u.getUserName(), u.getContrasena(), authorities);
    }

}
