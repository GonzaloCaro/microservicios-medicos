package com.gestion_usuarios.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestion_usuarios.DTO.AreaDTO;
import com.gestion_usuarios.hateoas.AreaModelAssembler;
import com.gestion_usuarios.mapper.AreaMapper;
import com.gestion_usuarios.model.Area;
import com.gestion_usuarios.service.AreaService;
import com.gestion_usuarios.service.CustomUserDetailsService;
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

@WebMvcTest(AreaController.class)
@AutoConfigureMockMvc(addFilters = false) // Desactiva seguridad (Login)
class AreaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // --- Mocks de Lógica de Negocio ---
    @MockBean
    private AreaService areaService;

    @MockBean
    private AreaMapper areaMapper;

    @MockBean
    private AreaModelAssembler areaModelAssembler;

    // --- Mocks de Infraestructura (Vitales para que cargue el contexto) ---
    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    private Area area;
    private AreaDTO areaDTO;
    private UUID id;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();

        area = new Area();
        area.setId(id);
        area.setNombre("Recursos Humanos");

        areaDTO = new AreaDTO();
        areaDTO.setNombre("Recursos Humanos");
    }

    // --- TEST: GET ALL ---
    @Test
    void getAllAreas_ConDatos_Retorna200() throws Exception {
        // GIVEN
        List<Area> lista = List.of(area);
        when(areaService.getAllAreas()).thenReturn(lista);
        when(areaModelAssembler.toModel(any(Area.class))).thenReturn(EntityModel.of(area));

        // WHEN & THEN
        mockMvc.perform(get("/api/areas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.areaList", hasSize(1)))
                .andExpect(jsonPath("$._embedded.areaList[0].nombre", is("Recursos Humanos")));
    }

    @Test
    void getAllAreas_Vacio_Retorna204() throws Exception {
        // GIVEN
        when(areaService.getAllAreas()).thenReturn(Collections.emptyList());

        // WHEN & THEN
        mockMvc.perform(get("/api/areas"))
                .andExpect(status().isNoContent()); // 204 No Content
    }

    // --- TEST: GET BY ID ---
    @Test
    void getAreaById_Exito_Retorna200() throws Exception {
        // GIVEN
        when(areaService.getAreaById(id)).thenReturn(area);
        when(areaModelAssembler.toModel(area)).thenReturn(EntityModel.of(area));

        // WHEN & THEN
        mockMvc.perform(get("/api/areas/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Recursos Humanos")));
    }

    // --- TEST: POST (CREATE) ---
    @Test
    void createArea_Exito_Retorna201() throws Exception {
        // GIVEN
        // 1. El controller llama al mapper
        when(areaMapper.toEntity(any(AreaDTO.class))).thenReturn(area);
        // 2. Luego llama al servicio
        when(areaService.createArea(any(Area.class))).thenReturn(area);
        // 3. Luego llama al assembler
        when(areaModelAssembler.toModel(any(Area.class))).thenReturn(EntityModel.of(area));

        // WHEN & THEN
        mockMvc.perform(post("/api/areas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(areaDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre", is("Recursos Humanos")));
    }

    @Test
    void createArea_Falla_Retorna500() throws Exception {
        // GIVEN: Simulamos error en el servicio
        when(areaMapper.toEntity(any(AreaDTO.class))).thenReturn(area);
        when(areaService.createArea(any(Area.class))).thenThrow(new RuntimeException("Error BD"));

        // WHEN & THEN
        // Tu controller tiene un try-catch que devuelve
        // ResponseEntity.status(500).build()
        mockMvc.perform(post("/api/areas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(areaDTO)))
                .andExpect(status().isInternalServerError()); // 500
    }

    @Test
    void createArea_DtoNulo_Retorna400() throws Exception {
        // Spring valida el body antes de entrar al método si no hay body
        mockMvc.perform(post("/api/areas")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    // --- TEST: DELETE ---
    @Test
    void deleteArea_Exito_Retorna200() throws Exception {
        // GIVEN
        doNothing().when(areaService).deleteArea(id);

        // WHEN & THEN
        mockMvc.perform(delete("/api/areas/{id}", id))
                .andExpect(status().isOk());
    }

    @Test
    void deleteArea_Falla_Retorna500() throws Exception {
        // GIVEN
        doThrow(new RuntimeException("Error al borrar")).when(areaService).deleteArea(id);

        // WHEN & THEN
        // Tu controller atrapa la excepción y devuelve 500
        mockMvc.perform(delete("/api/areas/{id}", id))
                .andExpect(status().isInternalServerError());
    }
}