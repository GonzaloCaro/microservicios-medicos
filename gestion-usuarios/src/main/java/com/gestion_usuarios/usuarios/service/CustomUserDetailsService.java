package com.gestion_usuarios.usuarios.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.gestion_usuarios.usuarios.model.Usuario;
import com.gestion_usuarios.usuarios.repository.UsuarioRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUserName(userName)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + userName));

        String nombreRol = usuario.getRole().getRol().getNombre();

        return org.springframework.security.core.userdetails.User.builder()
                .username(usuario.getUserName())
                .password(usuario.getContrasena())
                .roles(nombreRol)
                .build();
    }
}