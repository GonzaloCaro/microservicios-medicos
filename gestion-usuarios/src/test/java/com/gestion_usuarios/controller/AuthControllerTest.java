package com.gestion_usuarios.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestion_usuarios.DTO.LoginRequest;
import com.gestion_usuarios.model.Area;
import com.gestion_usuarios.model.Rol;
import com.gestion_usuarios.model.RoleUser;
import com.gestion_usuarios.model.Usuario;
import com.gestion_usuarios.repository.UsuarioRepository;
import com.gestion_usuarios.service.CustomUserDetailsService;
import com.gestion_usuarios.utils.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print; // IMPORTANTE PARA DEBUG
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    private LoginRequest loginRequest;
    private Usuario usuarioCompleto;
    private String jwtToken;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
        loginRequest.setUserName("admin");
        loginRequest.setPassword("12345");

        jwtToken = "eyJhbGciOiJIUzI1NiJ9.fake-token";

        // Setup de usuario completo
        UUID roleId = UUID.randomUUID();
        UUID areaId = UUID.randomUUID();

        Rol rol = new Rol();
        rol.setId(roleId);
        rol.setNombre("ADMIN");

        Area area = new Area();
        area.setId(areaId);
        area.setNombre("IT");

        RoleUser roleUser = new RoleUser();
        roleUser.setRol(rol);
        roleUser.setArea(area);

        usuarioCompleto = new Usuario();
        usuarioCompleto.setId(UUID.randomUUID());
        usuarioCompleto.setUserName("admin");
        usuarioCompleto.setNombre("Admin");
        usuarioCompleto.setApellido("System");
        usuarioCompleto.setEmail("admin@test.com");
        usuarioCompleto.setRole(roleUser);
    }

    @Test
    void authenticateUser_CredencialesValidas_RetornaTokenYCookie() throws Exception {
        // GIVEN
        Authentication authMock = mock(Authentication.class);
        UserDetails userDetails = new User("admin", "12345", Collections.emptyList());

        when(authMock.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authMock);
        when(jwtUtils.generateJwtToken(any(UserDetails.class))).thenReturn(jwtToken);
        when(usuarioRepository.findByUserName("admin")).thenReturn(Optional.of(usuarioCompleto));

        // WHEN & THEN
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(cookie().value("jwtToken", jwtToken))
                .andExpect(jsonPath("$.userName", is("admin")));
    }

    @Test
    void authenticateUser_UsuarioSinRol_NoFallaYRetornaNulos() throws Exception {
        Usuario usuarioBasico = new Usuario();
        usuarioBasico.setId(UUID.randomUUID());
        usuarioBasico.setUserName("user");
        usuarioBasico.setRole(null);

        Authentication authMock = mock(Authentication.class);
        UserDetails userDetails = new User("user", "12345", Collections.emptyList());

        when(authMock.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any())).thenReturn(authMock);
        when(jwtUtils.generateJwtToken(any())).thenReturn(jwtToken);
        when(usuarioRepository.findByUserName("admin")).thenReturn(Optional.of(usuarioBasico));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                // Verificamos que no explote
                .andExpect(jsonPath("$.roleNombre", nullValue()));
    }
}