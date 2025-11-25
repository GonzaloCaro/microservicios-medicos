package com.gestion_usuarios.usuarios.controller;

import java.util.UUID;

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
import com.gestion_usuarios.usuarios.model.Area;
import com.gestion_usuarios.usuarios.model.Rol;
import com.gestion_usuarios.usuarios.model.RoleUser;
import com.gestion_usuarios.usuarios.model.Usuario;
import com.gestion_usuarios.usuarios.repository.UsuarioRepository;
import com.gestion_usuarios.usuarios.utils.JwtUtils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
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
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest,
            HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUserName(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtUtils.generateJwtToken((UserDetails) authentication.getPrincipal());

        Usuario usuario = usuarioRepository.findByUserName(loginRequest.getUserName()).orElse(null);

        String nombre = null;
        String apellido = null;
        String email = null;
        UUID roleId = null;
        String roleNombre = null;
        UUID areaId = null;
        String areaNombre = null;

        if (usuario != null) {
            nombre = usuario.getNombre();
            apellido = usuario.getApellido();
            email = usuario.getEmail();

            RoleUser roleUser = usuario.getRole();
            if (roleUser != null) {
                Rol rol = roleUser.getRol();
                Area area = roleUser.getArea();

                if (rol != null) {
                    roleId = rol.getId();
                    roleNombre = rol.getNombre();
                }

                if (area != null) {
                    areaId = area.getId();
                    areaNombre = area.getNombre();
                }
            }
        }
        Cookie cookie = new Cookie("jwtToken", jwt);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60); // 1 hora
        response.addCookie(cookie);

        LoginResponse resp = new LoginResponse(
                jwt,
                usuario != null ? usuario.getId() : null,
                loginRequest.getUserName(),
                nombre,
                apellido,
                email,
                roleId,
                roleNombre,
                areaId,
                areaNombre);

        return ResponseEntity.ok(resp);
    }
}
