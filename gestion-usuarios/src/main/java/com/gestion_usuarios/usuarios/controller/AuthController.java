package com.gestion_usuarios.usuarios.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gestion_usuarios.usuarios.DTO.LoginRequest;
import com.gestion_usuarios.usuarios.DTO.response.LoginResponse;
import com.gestion_usuarios.usuarios.model.Usuario;
import com.gestion_usuarios.usuarios.repository.UsuarioRepository;
import com.gestion_usuarios.usuarios.utils.JwtUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UsuarioRepository usuarioRepository;

    public AuthController(AuthenticationManager authenticationManager, JwtUtils jwtUtils,
            UsuarioRepository usuarioRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUserName(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtUtils.generateJwtToken((UserDetails) authentication.getPrincipal());

        Usuario usuario = usuarioRepository.findByUserName(loginRequest.getUserName()).orElse(null);

        LoginResponse resp = new LoginResponse(
                jwt,
                usuario != null ? usuario.getId() : null,
                loginRequest.getUserName());

        return ResponseEntity.ok(resp);
    }
}
