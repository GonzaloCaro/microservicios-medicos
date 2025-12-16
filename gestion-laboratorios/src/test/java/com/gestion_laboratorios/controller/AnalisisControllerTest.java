package com.gestion_laboratorios.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestion_laboratorios.DTO.AnalisisDTO;
import com.gestion_laboratorios.exception.ResourceNotFoundException;
import com.gestion_laboratorios.hateoas.AnalisisModelAssembler;
import com.gestion_laboratorios.mapper.AnalisisMapper;
import com.gestion_laboratorios.model.Analisis;
import com.gestion_laboratorios.service.AnalisisService;
import com.gestion_laboratorios.utils.JwtUtils; // Necesario para evitar error de carga de contexto
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AnalisisController.class)
@AutoConfigureMockMvc(addFilters = false) // Desactiva seguridad para enfocarse en el controller
class AnalisisControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // --- Mocks de Dependencias del Controller ---
    @MockBean
    private AnalisisService analisisService;

    @MockBean
    private AnalisisMapper analisisMapper;

    @MockBean
    private AnalisisModelAssembler analisisModelAssembler;

    // --- Mock Crucial para que SecurityConfig no falle al iniciar ---
    @MockBean
    private JwtUtils jwtUtils;

    private Analisis analisis;
    private AnalisisDTO analisisDTO;
    private UUID id;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();

        analisis = new Analisis();
        analisis.setId(id);
        analisis.setCodigo("A001");
        analisis.setNombre("Hemograma");

        analisisDTO = new AnalisisDTO();
        analisisDTO.setCodigo("A001");
        analisisDTO.setNombre("Hemograma");
    }

    // --- TEST: GET ALL ---
    @Test
    void getAllAnalisis_Exito() throws Exception {
        // GIVEN
        List<Analisis> lista = List.of(analisis);
        when(analisisService.getAllAnalisis()).thenReturn(lista);
        // Mockeamos el assembler para devolver un EntityModel simple
        when(analisisModelAssembler.toModel(any(Analisis.class))).thenReturn(EntityModel.of(analisis));

        // WHEN & THEN
        mockMvc.perform(get("/api/analisis"))
                .andExpect(status().isOk())
                // HATEOAS suele envolver la lista en "_embedded.analisisList"
                .andExpect(jsonPath("$._embedded.analisisList", hasSize(1)))
                .andExpect(jsonPath("$._embedded.analisisList[0].codigo", is("A001")));
    }

    @Test
    void getAllAnalisis_Vacio_LanzaExcepcion() throws Exception {
        // GIVEN
        when(analisisService.getAllAnalisis()).thenReturn(Collections.emptyList());

        // WHEN & THEN
        mockMvc.perform(get("/api/analisis"))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException));
    }

    // --- TEST: GET BY ID ---
    @Test
    void getAnalisisById_Exito() throws Exception {
        // GIVEN
        when(analisisService.getAnalisisById(id)).thenReturn(analisis);
        when(analisisModelAssembler.toModel(analisis)).thenReturn(EntityModel.of(analisis));

        // WHEN & THEN
        mockMvc.perform(get("/api/analisis/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo", is("A001")));
    }

    @Test
    void getAnalisisById_NoEncontrado() throws Exception {
        // GIVEN: Simulamos que el servicio lanza excepción (o retorna null, según
        // implementación)
        // En tu controller llamas a service.getAnalisisById(id). Si el servicio lanza
        // runtime exception,
        // MockMvc la capturará. Si tu servicio lanza ResourceNotFoundException, mejor.

        when(analisisService.getAnalisisById(id)).thenThrow(new ResourceNotFoundException("No encontrado"));

        // WHEN & THEN
        mockMvc.perform(get("/api/analisis/{id}", id))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException));
    }

    // --- TEST: POST (CREATE) ---
    @Test
    void createAnalisis_Exito() throws Exception {
        // GIVEN
        // El controller recibe DTO -> llama service -> devuelve Entity
        when(analisisService.createAnalisis(any(AnalisisDTO.class))).thenReturn(analisis);
        when(analisisModelAssembler.toModel(any(Analisis.class))).thenReturn(EntityModel.of(analisis));

        // WHEN & THEN
        mockMvc.perform(post("/api/analisis")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(analisisDTO)))
                .andExpect(status().isOk()) // Tu código devuelve ResponseEntity.ok() en create
                .andExpect(jsonPath("$.codigo", is("A001")));
    }

    // --- TEST: PUT (UPDATE) ---
    @Test
    void updateAnalisis_Exito() throws Exception {
        // GIVEN
        Analisis actualizado = new Analisis();
        actualizado.setId(id);
        actualizado.setCodigo("B002");
        actualizado.setNombre("Update");

        AnalisisDTO dtoUpdate = new AnalisisDTO();
        dtoUpdate.setCodigo("B002");
        dtoUpdate.setNombre("Update");

        // Mockeamos el Mapper porque tu controller lo usa explícitamente:
        // analisisMapper.toEntity(dto)
        when(analisisMapper.toEntity(any(AnalisisDTO.class))).thenReturn(actualizado);
        when(analisisService.updateAnalisis(eq(id), any(Analisis.class))).thenReturn(actualizado);
        when(analisisModelAssembler.toModel(actualizado)).thenReturn(EntityModel.of(actualizado));

        // WHEN & THEN
        mockMvc.perform(put("/api/analisis/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo", is("B002")));
    }

    // --- TEST: DELETE ---
    @Test
    void deleteAnalisis_Exito() throws Exception {
        // GIVEN
        doNothing().when(analisisService).deleteAnalisis(id);

        // WHEN & THEN
        mockMvc.perform(delete("/api/analisis/{id}", id))
                .andExpect(status().isNoContent()); // Esperamos 204 No Content
    }
}