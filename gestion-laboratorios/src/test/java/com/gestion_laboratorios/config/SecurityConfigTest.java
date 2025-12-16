package com.gestion_laboratorios.config;

import com.gestion_laboratorios.utils.JwtUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest; // <--- IMPORTANTE: Usar este Mock
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Registramos explícitamente el Controller de prueba y la Configuración
@WebMvcTest(controllers = SecurityConfigTest.TestController.class)
@Import({ SecurityConfig.class, SecurityConfigTest.TestController.class })
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUtils jwtUtils;

    @Autowired
    private SecurityConfig securityConfig;

    // --- TEST 1: Verificar reglas de URLs ---

    @Test
    void requestPrivado_SinToken_DeberiaDar403() throws Exception {
        mockMvc.perform(get("/test"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void requestPrivado_ConUsuarioAutenticado_DeberiaDar200() throws Exception {
        mockMvc.perform(get("/test"))
                .andExpect(status().isOk());
    }

    @Test
    void requestOptions_DeberiaEstarPermitido_SinToken() throws Exception {
        mockMvc.perform(options("/test"))
                .andExpect(status().isOk());
    }

    // --- TEST 2: Verificar Configuración CORS (Integración HTTP) ---

    @Test
    void cors_DeberiaPermitirOrigenLocalhost() throws Exception {
        mockMvc.perform(options("/test")
                .header(HttpHeaders.ORIGIN, "http://localhost:4200")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:4200"));
    }

    @Test
    void cors_DeberiaBloquearOrigenDesconocido() throws Exception {
        mockMvc.perform(options("/test")
                .header(HttpHeaders.ORIGIN, "http://malicious-site.com")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET"))
                .andExpect(status().isForbidden());
    }

    // --- TEST 3: Unitario Puro del Bean CORS Configuration ---

    @Test
    void corsConfigurationSource_DeberiaTenerConfiguracionCorrecta() {
        // GIVEN
        CorsConfigurationSource source = securityConfig.corsConfigurationSource();

        // SOLUCIÓN AL NPE: Usamos MockHttpServletRequest de Spring, no un mock() de
        // Mockito.
        // Este objeto sí tiene implementación real de métodos como getRequestURI()
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.setRequestURI("/test");

        // WHEN
        CorsConfiguration config = source.getCorsConfiguration(request);

        // THEN
        assertNotNull(config, "La configuración CORS no debería ser nula");
        assertTrue(config.getAllowedOrigins().contains("http://localhost:4200"));
        assertTrue(config.getAllowedMethods().containsAll(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")));
        assertTrue(config.getAllowCredentials());
        assertTrue(config.getAllowedHeaders().contains("Authorization"));
        assertEquals(3600L, config.getMaxAge());
    }

    // --- CONTROLLER DE PRUEBA ---
    @RestController
    static class TestController {
        @GetMapping("/test")
        public String testEndpoint() {
            return "Acceso Concedido";
        }
    }
}