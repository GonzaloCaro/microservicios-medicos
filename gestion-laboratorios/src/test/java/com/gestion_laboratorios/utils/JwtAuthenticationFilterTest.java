package com.gestion_laboratorios.utils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    // Usamos MockHttpServletRequest de Spring Test, es mucho más fácil que mockear
    // la interfaz
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        // Importante: Limpiar el contexto de seguridad antes de cada test para evitar
        // contaminación
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        // Limpiamos también después
        SecurityContextHolder.clearContext();
    }

    // --- CASO 1: Token Válido ---
    @Test
    void doFilterInternal_ConTokenValido_DeberiaAutenticarUsuario() throws ServletException, IOException {
        // GIVEN
        String token = "tokenValido123";
        String username = "usuarioTest";

        // Simulamos que el header viene correcto
        request.addHeader("Authorization", "Bearer " + token);

        // Simulamos el comportamiento de JwtUtils
        when(jwtUtils.validateJwtToken(token)).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken(token)).thenReturn(username);

        // WHEN
        // Llamamos al método protegido (funciona porque estamos en el mismo paquete en
        // test)
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // THEN
        // 1. Verificamos que se haya establecido la autenticación en el contexto
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth, "La autenticación no debería ser nula");
        assertEquals(username, auth.getPrincipal());

        // 2. Verificamos que la cadena de filtros continúe
        verify(filterChain, times(1)).doFilter(request, response);
    }

    // --- CASO 2: Token Inválido ---
    @Test
    void doFilterInternal_ConTokenInvalido_NoDeberiaAutenticar() throws ServletException, IOException {
        // GIVEN
        String token = "tokenInvalido";
        request.addHeader("Authorization", "Bearer " + token);

        when(jwtUtils.validateJwtToken(token)).thenReturn(false);

        // WHEN
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // THEN
        // 1. El contexto debe estar vacío
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        // 2. Pero la cadena debe continuar (hacia el 403 Forbidden manejado por
        // SecurityConfig)
        verify(filterChain, times(1)).doFilter(request, response);
    }

    // --- CASO 3: Sin Header de Autorización ---
    @Test
    void doFilterInternal_SinHeader_NoDeberiaHacerNada() throws ServletException, IOException {
        // GIVEN
        // No agregamos ningún header al request

        // WHEN
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // THEN
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
        verifyNoInteractions(jwtUtils); // No debería ni llamar al utils si no hay header
    }

    // --- CASO 4: Header con formato incorrecto (Sin Bearer) ---
    @Test
    void doFilterInternal_HeaderSinBearer_NoDeberiaHacerNada() throws ServletException, IOException {
        // GIVEN
        request.addHeader("Authorization", "Basic 12345"); // Formato incorrecto para este filtro

        // WHEN
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // THEN
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
        verifyNoInteractions(jwtUtils);
    }
}