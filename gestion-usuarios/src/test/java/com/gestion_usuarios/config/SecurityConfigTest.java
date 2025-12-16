package com.gestion_usuarios.config;

import com.gestion_usuarios.service.CustomUserDetailsService;
import com.gestion_usuarios.utils.JwtUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Mockeamos las dependencias que SecurityConfig pide en su constructor
    // para que el contexto de Spring levante sin conectar a la BD real.
    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    // --- 1. TEST DE RUTAS PÚBLICAS (permitAll) ---
    @Test
    void accesoPublico_ApiAuth_NoDeberiaRetornar401() throws Exception {
        // Tu configuración dice: .requestMatchers("/api/auth/**").permitAll()
        // Si la seguridad funciona, no debe dar 401.
        // Puede dar 404 (si no existe el controller en el test) o 200, pero NO 401/403.

        mockMvc.perform(get("/api/auth/login"))
                .andExpect(status().is(org.hamcrest.Matchers.not(401)))
                .andExpect(status().is(org.hamcrest.Matchers.not(403)));
    }

    @Test
    void accesoPublico_ApiUsuarios_NoDeberiaRetornar401() throws Exception {
        // Según tu código actual: "/api/usuarios/**" está en permitAll()
        mockMvc.perform(get("/api/usuarios/listar"))
                .andExpect(status().is(org.hamcrest.Matchers.not(401)));
    }

    @Test
    void accesoPublico_Actuator_NoDeberiaRetornar401() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().is(org.hamcrest.Matchers.not(401)));
    }

    @Test
    void accesoPrivado_RutaDesconocida_DeberiaRetornar403() throws Exception {
        // Intentamos acceder sin token a una ruta protegida
        mockMvc.perform(get("/api/ruta-super-secreta/admin"))
                .andExpect(status().isForbidden());
    }

    // --- 3. TEST DE CORS ---
    @Test
    void cors_DeberiaPermitirOrigenLocalhost4200() throws Exception {
        // Simulamos una petición "preflight" (OPTIONS) desde Angular (localhost:4200)
        mockMvc.perform(options("/api/auth/login")
                .header(HttpHeaders.ORIGIN, "http://localhost:4200")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST"))
                .andExpect(status().isOk()) // El preflight debe responder OK
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:4200"))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET,POST,PUT,DELETE,OPTIONS"));
    }

    @Test
    void cors_DeberiaBloquearOrigenDesconocido() throws Exception {
        // Si intentamos desde un origen no listado en tu config
        mockMvc.perform(options("/api/auth/login")
                .header(HttpHeaders.ORIGIN, "http://hacker-site.com")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST"))
                // Spring Security por defecto no añade el header Allow-Origin si el origen no
                // está permitido.
                // Ojo: Dependiendo de la versión, puede devolver 403 o simplemente ignorar el
                // header CORS.
                .andExpect(header().doesNotExist(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
    }

    // --- 4. TEST DE BEANS ---
    @Test
    void passwordEncoder_DeberiaSerBCrypt() {
        // Verificamos que el Bean inyectado sea efectivamente BCrypt
        assertThat(passwordEncoder).isInstanceOf(BCryptPasswordEncoder.class);

        // Verificamos que encripte correctamente (siempre genera hashes diferentes)
        String rawPassword = "password123";
        String encoded1 = passwordEncoder.encode(rawPassword);
        String encoded2 = passwordEncoder.encode(rawPassword);

        assertThat(encoded1).isNotEqualTo(encoded2); // BCrypt usa sal aleatoria
        assertThat(passwordEncoder.matches(rawPassword, encoded1)).isTrue();
    }
}