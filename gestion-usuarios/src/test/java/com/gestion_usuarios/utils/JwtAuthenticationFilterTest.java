package com.gestion_usuarios.utils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        // Aseguramos que el contexto de seguridad esté limpio antes de cada test
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        // Limpiamos el contexto después de cada test para evitar efectos secundarios
        SecurityContextHolder.clearContext();
    }

    // --- CASO 1: Token Válido (Happy Path) ---
    @Test
    void doFilterInternal_TokenValido_DeberiaAutenticarUsuario() throws ServletException, IOException {
        // GIVEN
        String token = "valid.token.here";
        String username = "admin";

        // 1. Simulamos el Header Authorization
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        // 2. Simulamos validación positiva del token
        when(jwtUtils.validateJwtToken(token)).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken(token)).thenReturn(username);

        // 3. Simulamos carga de usuario
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getAuthorities()).thenReturn(Collections.emptyList()); // Sin roles por ahora
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);

        // WHEN
        // Llamamos al método público doFilter, que internamente llama a
        // doFilterInternal
        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        // THEN
        // A. Verificamos que se haya establecido la autenticación en el contexto
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth, "La autenticación no debería ser nula");
        assertEquals(userDetails, auth.getPrincipal());

        // B. Verificamos que la cadena de filtros continúe
        verify(filterChain, times(1)).doFilter(request, response);
    }

    // --- CASO 2: Token Inválido ---
    @Test
    void doFilterInternal_TokenInvalido_NoDeberiaAutenticar() throws ServletException, IOException {
        // GIVEN
        String token = "invalid.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        // Simulamos que el token NO es válido
        when(jwtUtils.validateJwtToken(token)).thenReturn(false);

        // WHEN
        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        // THEN
        // A. El contexto debe seguir vacío
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        // B. Pero la cadena debe continuar (hacia el error 401/403 manejado por
        // SecurityConfig)
        verify(filterChain, times(1)).doFilter(request, response);

        // C. No se debió intentar cargar el usuario
        verify(userDetailsService, never()).loadUserByUsername(anyString());
    }

    // --- CASO 3: Sin Header Authorization ---
    @Test
    void doFilterInternal_SinHeader_NoHacerNada() throws ServletException, IOException {
        // GIVEN
        when(request.getHeader("Authorization")).thenReturn(null);

        // WHEN
        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        // THEN
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
        verifyNoInteractions(jwtUtils); // No se debió llamar al utils
    }

    // --- CASO 4: Header con Formato Incorrecto (Sin "Bearer ") ---
    @Test
    void doFilterInternal_HeaderSinBearer_NoHacerNada() throws ServletException, IOException {
        // GIVEN
        when(request.getHeader("Authorization")).thenReturn("Basic 12345");

        // WHEN
        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        // THEN
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
        verifyNoInteractions(jwtUtils);
    }
}