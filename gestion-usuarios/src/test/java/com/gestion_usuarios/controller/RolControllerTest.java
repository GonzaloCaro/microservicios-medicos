package com.gestion_usuarios.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestion_usuarios.hateoas.RolModelAssembler;
import com.gestion_usuarios.mapper.RolMapper;
import com.gestion_usuarios.model.Rol;
import com.gestion_usuarios.service.CustomUserDetailsService;
import com.gestion_usuarios.service.RolService;
import com.gestion_usuarios.utils.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RolController.class)
@AutoConfigureMockMvc(addFilters = false) // Desactiva filtros de seguridad para el test
class RolControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // --- Mocks de la Lógica del Controller ---
    @MockBean
    private RolService rolService;

    @MockBean
    private RolMapper rolMapper; // Se inyecta en el constructor, hay que mockearlo aunque no se use mucho

    @MockBean
    private RolModelAssembler rolModelAssembler;

    // --- Mocks de Infraestructura (Vitales para SecurityConfig) ---
    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    private Rol rol;
    private UUID id;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        rol = new Rol();
        rol.setId(id);
        rol.setNombre("ROLE_ADMIN");
    }

    // --- TEST: GET ALL ---
    @Test
    void getAllRoles_ConDatos_Retorna200() throws Exception {
        // GIVEN
        List<Rol> lista = List.of(rol);
        when(rolService.getAllRoles()).thenReturn(lista);
        // Mockeamos HATEOAS
        when(rolModelAssembler.toModel(any(Rol.class))).thenReturn(EntityModel.of(rol));

        // WHEN & THEN
        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isOk())
                // HATEOAS envuelve la lista en "_embedded"
                .andExpect(jsonPath("$._embedded.rolList", hasSize(1)))
                .andExpect(jsonPath("$._embedded.rolList[0].nombre", is("ROLE_ADMIN")));
    }

    @Test
    void getAllRoles_Vacio_Retorna204() throws Exception {
        // GIVEN
        when(rolService.getAllRoles()).thenReturn(Collections.emptyList());

        // WHEN & THEN
        // Tu controller tiene lógica explícita: si está vacío -> noContent (204)
        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isNoContent());
    }

    // --- TEST: GET BY ID ---
    @Test
    void getRolById_Exito_Retorna200() throws Exception {
        // GIVEN
        when(rolService.getRolById(id)).thenReturn(rol);
        when(rolModelAssembler.toModel(rol)).thenReturn(EntityModel.of(rol));

        // WHEN & THEN
        mockMvc.perform(get("/api/roles/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("ROLE_ADMIN")));
    }

    // --- TEST: POST (CREATE) ---
    @Test
    void createRol_Exito_Retorna201() throws Exception {
        // GIVEN
        // Tu controller recibe directamente la entidad Rol en el Body
        when(rolService.createRol(any(Rol.class))).thenReturn(rol);
        when(rolModelAssembler.toModel(any(Rol.class))).thenReturn(EntityModel.of(rol));

        // WHEN & THEN
        mockMvc.perform(post("/api/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rol)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre", is("ROLE_ADMIN")));
    }

    @Test
    void createRol_Nulo_BadRequest() throws Exception {
        // Enviamos JSON vacío o inválido
        mockMvc.perform(post("/api/roles")
                .contentType(MediaType.APPLICATION_JSON))
                // Sin .content(...) envía body vacío -> 400 Bad Request
                .andExpect(status().isBadRequest());
    }

    // --- TEST: DELETE ---
    @Test
    void deleteRol_Exito_Retorna200YWrapper() throws Exception {
        // GIVEN
        doNothing().when(rolService).deleteRol(id);

        // WHEN & THEN
        mockMvc.perform(delete("/api/roles/{id}", id))
                .andExpect(status().isOk());
    }
}