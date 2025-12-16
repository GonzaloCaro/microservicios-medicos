package com.gestion_usuarios.utils;

import io.jsonwebtoken.io.Encoders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtUtilsTest {

    @InjectMocks
    private JwtUtils jwtUtils;

    @Mock
    private UserDetails userDetails;

    // Generamos una clave segura para el test (HS256 requiere 32 bytes / 256 bits)
    // "mi-clave-secreta" es muy corta, así que creamos una dinámica fuerte para el
    // test.
    private String secretKeyBase64;
    private final long expirationMs = 3600000; // 1 hora

    @BeforeEach
    void setUp() {
        // 1. Generar una clave válida para HS256
        byte[] keyBytes = new byte[32];
        new SecureRandom().nextBytes(keyBytes);
        secretKeyBase64 = Encoders.BASE64.encode(keyBytes);

        // 2. Inyectar los valores privados usando ReflectionTestUtils
        // Esto simula lo que hace @Value en Spring
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", secretKeyBase64);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", expirationMs);
    }

    // --- TEST: Generación de Token ---
    @Test
    void generateJwtToken_DeberiaCrearTokenValido() {
        // GIVEN
        String username = "usuario_test";
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));

        when(userDetails.getUsername()).thenReturn(username);

        // WHEN
        String token = jwtUtils.generateJwtToken(userDetails);

        // THEN
        assertNotNull(token);
        assertTrue(token.length() > 0);
        // Un JWT tiene 3 partes separadas por puntos
        assertEquals(3, token.split("\\.").length);
    }

    // --- TEST: Obtener Usuario del Token ---
    @Test
    void getUserNameFromJwtToken_DeberiaExtraerUsername() {
        // GIVEN
        String username = "usuario_admin";
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));

        when(userDetails.getUsername()).thenReturn(username);

        // Generamos un token real primero
        String token = jwtUtils.generateJwtToken(userDetails);

        // WHEN
        String extractedUsername = jwtUtils.getUserNameFromJwtToken(token);

        // THEN
        assertEquals(username, extractedUsername);
    }

    // --- TEST: Validación (Token Válido) ---
    @Test
    void validateJwtToken_TokenValido_RetornaTrue() {
        // GIVEN
        when(userDetails.getUsername()).thenReturn("test");

        String token = jwtUtils.generateJwtToken(userDetails);

        // WHEN
        boolean isValid = jwtUtils.validateJwtToken(token);

        // THEN
        assertTrue(isValid);
    }

    // --- TEST: Validación (Token Inválido / Firma Rota) ---
    @Test
    void validateJwtToken_FirmaInvalida_RetornaFalse() {
        // GIVEN
        when(userDetails.getUsername()).thenReturn("test");

        String token = jwtUtils.generateJwtToken(userDetails);

        // Manipulamos el token (cambiamos el último caracter para romper la firma)
        String tokenAdulterado = token.substring(0, token.length() - 1) + "X";

        // WHEN
        boolean isValid = jwtUtils.validateJwtToken(tokenAdulterado);

        // THEN
        assertFalse(isValid);
    }

    // --- TEST: Validación (Token Expirado) ---
    @Test
    void validateJwtToken_TokenExpirado_RetornaFalse() {
        // GIVEN
        // Truco: Re-inyectamos un tiempo de expiración negativo (-1000ms)
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", -1000L);

        when(userDetails.getUsername()).thenReturn("expired_user");

        // Al generar el token, la fecha de expiración será (Ahora - 1 segundo)
        String expiredToken = jwtUtils.generateJwtToken(userDetails);

        // Importante: Volver a poner la configuración "normal" para validar (aunque
        // validate verifica contra la fecha actual)
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", expirationMs);

        // WHEN
        boolean isValid = jwtUtils.validateJwtToken(expiredToken);

        // THEN
        assertFalse(isValid, "El token debería ser inválido porque ya expiró");
    }

    // --- TEST: Validación (Token Malformado/Vacío) ---
    @Test
    void validateJwtToken_TokenMalformado_RetornaFalse() {
        assertFalse(jwtUtils.validateJwtToken("esto.no.es.un.token"));
        assertFalse(jwtUtils.validateJwtToken(""));
        assertFalse(jwtUtils.validateJwtToken(null));
    }

    // Helper para evitar warnings de unchecked cast con Mockito
    @SuppressWarnings("unchecked")
    private <T> T doReturn(Object toReturn) {
        return (T) org.mockito.Mockito.doReturn(toReturn);
    }
}