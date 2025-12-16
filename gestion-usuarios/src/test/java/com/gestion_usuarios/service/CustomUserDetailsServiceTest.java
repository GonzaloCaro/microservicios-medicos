package com.gestion_usuarios.service;

import com.gestion_usuarios.model.Rol;
import com.gestion_usuarios.model.RoleUser;
import com.gestion_usuarios.model.Usuario;
import com.gestion_usuarios.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    // --- CASO 1: Usuario Encontrado y con Rol ---
    @Test
    void loadUserByUsername_DeberiaRetornarUserDetails_CuandoExisteUsuario() {
        // GIVEN
        String username = "admin";

        // Construimos la cadena de objetos necesaria para evitar NullPointerException
        // Usuario -> AsignacionRol -> Rol -> Nombre
        Rol rol = new Rol();
        rol.setNombre("ADMIN"); // Spring Security lo transformará a "ROLE_ADMIN"

        RoleUser asignacionRol = new RoleUser();
        asignacionRol.setRol(rol);

        Usuario usuario = new Usuario();
        usuario.setUserName(username);
        usuario.setContrasena("12345");
        usuario.setRole(asignacionRol); // Vinculamos la relación

        when(usuarioRepository.findByUserName(username)).thenReturn(Optional.of(usuario));

        // WHEN
        UserDetails result = customUserDetailsService.loadUserByUsername(username);

        // THEN
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals("12345", result.getPassword());

        // Verificamos Authorities.
        // NOTA: El builder .roles("ADMIN") de Spring Security agrega automáticamente el
        // prefijo "ROLE_"
        assertTrue(result.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    // --- CASO 2: Usuario No Encontrado ---
    @Test
    void loadUserByUsername_DeberiaLanzarExcepcion_CuandoNoExiste() {
        // GIVEN
        String username = "fantasma";
        when(usuarioRepository.findByUserName(username)).thenReturn(Optional.empty());

        // WHEN & THEN
        UsernameNotFoundException ex = assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername(username);
        });

        assertTrue(ex.getMessage().contains("Usuario no encontrado"));
    }

    // --- CASO 3: Usuario Existe pero Datos Incompletos (NPE) ---
    @Test
    void loadUserByUsername_SiFaltaRol_LanzaNullPointerException() {
        // GIVEN
        // Este test documenta el comportamiento actual de tu código.
        // Tu código hace: usuario.getRole().getRol()... sin verificar nulos antes.

        String username = "user_roto";
        Usuario usuarioSinRol = new Usuario();
        usuarioSinRol.setUserName(username);
        usuarioSinRol.setRole(null); // Esto causará el fallo

        when(usuarioRepository.findByUserName(username)).thenReturn(Optional.of(usuarioSinRol));

        // WHEN & THEN
        assertThrows(NullPointerException.class, () -> {
            customUserDetailsService.loadUserByUsername(username);
        });
    }
}