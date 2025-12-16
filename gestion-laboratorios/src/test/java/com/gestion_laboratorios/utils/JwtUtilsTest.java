package com.gestion_laboratorios.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {

    private JwtUtils jwtUtils;

    // Esta clave debe ser Base64 y lo suficientemente larga (min 256 bits para
    // HS256)
    // "EstaEsUnaClaveSecretaMuyLargaParaTestingEnBase64QueDebeSerDeAlMenos32Bytes"
    // codificado en Base64:
    private final String TEST_SECRET_KEY = "RXN0YUVzVW5hQ2xhdmVTZWNyZXRhTXV5TGFyZ2FQYXJhVGVzdGluZ0VuQmFzZTY0UXVlZGViZVNlckRlQWxNZW5vczMyQnl0ZXM=";

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        // INYECCIÓN MANUAL: Usamos reflexión para setear el @Value privado
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", TEST_SECRET_KEY);
    }

    // --- TEST: Validación Exitosa ---
    @Test
    void validateJwtToken_ConTokenValido_DeberiaRetornarTrue() {
        // GIVEN
        String token = generateTestToken("usuarioTest", TEST_SECRET_KEY, 10000); // Expira en 10s

        // WHEN
        boolean isValid = jwtUtils.validateJwtToken(token);

        // THEN
        assertTrue(isValid, "El token debería ser válido");
    }

    // --- TEST: Extracción de Usuario ---
    @Test
    void getUserNameFromJwtToken_DeberiaRetornarUsernameCorrecto() {
        // GIVEN
        String expectedUser = "admin";
        String token = generateTestToken(expectedUser, TEST_SECRET_KEY, 10000);

        // WHEN
        String username = jwtUtils.getUserNameFromJwtToken(token);

        // THEN
        assertEquals(expectedUser, username);
    }

    // --- TEST: Token Inválido (Firma incorrecta) ---
    @Test
    void validateJwtToken_ConFirmaIncorrecta_DeberiaRetornarFalse() {
        // GIVEN
        // Generamos un token con UNA CLAVE DISTINTA a la que tiene configurada el
        // servicio
        String otraClave = "T3RyYUNsYXZlU2VjcmV0YVBhcmFSb21wZXJLaUZpcm1hRGVsVG9rZW5QmFyYVRlc3Rpbmc=";
        String token = generateTestToken("usuarioTest", otraClave, 10000);

        // WHEN
        boolean isValid = jwtUtils.validateJwtToken(token);

        // THEN
        assertFalse(isValid, "El token debería ser inválido porque la firma no coincide");
    }

    // --- TEST: Token Expirado ---
    @Test
    void validateJwtToken_ConTokenExpirado_DeberiaRetornarFalse() {
        // GIVEN
        // Generamos un token que expiró hace 1 segundo (-1000 ms)
        String token = generateTestToken("usuarioTest", TEST_SECRET_KEY, -1000);

        // WHEN
        boolean isValid = jwtUtils.validateJwtToken(token);

        // THEN
        assertFalse(isValid, "El token debería ser inválido por expiración");
    }

    // --- TEST: Token Malformado ---
    @Test
    void validateJwtToken_ConBasura_DeberiaRetornarFalse() {
        // GIVEN
        String token = "esto.no.es.un.token.valido";

        // WHEN
        boolean isValid = jwtUtils.validateJwtToken(token);

        // THEN
        assertFalse(isValid, "El token debería ser inválido por formato incorrecto");
    }

    @Test
    void validateJwtToken_ConTokenVacio_DeberiaRetornarFalse() {
        assertFalse(jwtUtils.validateJwtToken(""));
        assertFalse(jwtUtils.validateJwtToken(null));
    }

    // =========================================================================
    // MÉTODO AUXILIAR PARA CREAR TOKENS DENTRO DEL TEST
    // =========================================================================
    private String generateTestToken(String username, String secret, long expirationMillis) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        Key key = Keys.hmacShaKeyFor(keyBytes);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}