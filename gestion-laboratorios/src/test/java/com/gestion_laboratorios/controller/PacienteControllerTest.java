package com.gestion_laboratorios.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestion_laboratorios.DTO.PacienteDTO;
import com.gestion_laboratorios.exception.ResourceNotFoundException;
import com.gestion_laboratorios.hateoas.PacienteModelAssembler;
import com.gestion_laboratorios.mapper.PacienteMapper;
import com.gestion_laboratorios.model.Paciente;
import com.gestion_laboratorios.service.PacienteService;
import com.gestion_laboratorios.utils.JwtUtils; // Importante para el fix de seguridad
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

@WebMvcTest(PacienteController.class)
@AutoConfigureMockMvc(addFilters = false) // Desactiva filtros de seguridad (Login/JWT)
class PacienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // --- Mocks de Lógica de Negocio ---
    @MockBean
    private PacienteService pacienteService;

    @MockBean
    private PacienteMapper pacienteMapper;

    @MockBean
    private PacienteModelAssembler pacienteModelAssembler;

    // --- Mock de Infraestructura (Vital para que no falle el contexto) ---
    @MockBean
    private JwtUtils jwtUtils;

    private Paciente paciente;
    private PacienteDTO pacienteDTO;
    private UUID id;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();

        paciente = new Paciente();
        paciente.setId(id);
        paciente.setNombrePaciente("Juan");
        paciente.setApellidoPaciente("Perez");
        paciente.setRut("12345678");

        pacienteDTO = new PacienteDTO();
        pacienteDTO.setNombrePaciente("Juan");
        pacienteDTO.setApellidoPaciente("Perez");
        // Agrega más datos al DTO si tienes validaciones @NotNull
    }

    // --- TEST: GET ALL ---
    @Test
    void getAllPacientes_Exito() throws Exception {
        // GIVEN
        List<Paciente> lista = List.of(paciente);
        when(pacienteService.getAllPacientes()).thenReturn(lista);
        
        // Mock del assembler HATEOAS
        when(pacienteModelAssembler.toModel(any(Paciente.class))).thenReturn(EntityModel.of(paciente));

        // WHEN & THEN
        mockMvc.perform(get("/api/paciente")) // Ojo: Ruta en singular según tu código
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.pacienteList", hasSize(1)))
                .andExpect(jsonPath("$._embedded.pacienteList[0].nombrePaciente", is("Juan")));
    }

    @Test
    void getAllPacientes_Vacio_LanzaExcepcion() throws Exception {
        // GIVEN
        when(pacienteService.getAllPacientes()).thenReturn(Collections.emptyList());

        // WHEN & THEN
        mockMvc.perform(get("/api/paciente"))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException));
    }

    // --- TEST: GET BY ID ---
    @Test
    void getPacienteById_Exito() throws Exception {
        // GIVEN
        when(pacienteService.getPacienteById(id)).thenReturn(paciente);
        when(pacienteModelAssembler.toModel(paciente)).thenReturn(EntityModel.of(paciente));

        // WHEN & THEN
        mockMvc.perform(get("/api/paciente/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombrePaciente", is("Juan")));
    }

    @Test
    void getPacienteById_NoEncontrado() throws Exception {
        // GIVEN: Simulamos que el servicio lanza la excepción
        when(pacienteService.getPacienteById(id)).thenThrow(new ResourceNotFoundException("Paciente no encontrado"));

        // WHEN & THEN
        mockMvc.perform(get("/api/paciente/{id}", id))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException));
    }

    // --- TEST: PUT (UPDATE) ---
    @Test
    void updatePaciente_Exito() throws Exception {
        // GIVEN
        Paciente pacienteActualizado = new Paciente();
        pacienteActualizado.setId(id);
        pacienteActualizado.setNombrePaciente("Juan Actualizado");

        PacienteDTO dtoUpdate = new PacienteDTO();
        dtoUpdate.setNombrePaciente("Juan Actualizado");

        // Mockeamos la cadena completa: Mapper -> Service -> Assembler
        when(pacienteMapper.toEntity(any(PacienteDTO.class))).thenReturn(paciente);
        when(pacienteService.updatePaciente(eq(id), any(Paciente.class))).thenReturn(pacienteActualizado);
        when(pacienteModelAssembler.toModel(pacienteActualizado)).thenReturn(EntityModel.of(pacienteActualizado));

        // WHEN & THEN
        mockMvc.perform(put("/api/paciente/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombrePaciente", is("Juan Actualizado")));
    }
    
    @Test
    void updatePaciente_IdNulo_BadRequest() throws Exception {
         // Aunque el path variable protege contra nulls en la URL, 
         // si mandamos un body nulo o malformado spring lanzará 400.
         mockMvc.perform(put("/api/paciente/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)) // Sin content body
                .andExpect(status().isBadRequest());
    }

    // --- TEST: DELETE ---
    @Test
    void deletePaciente_Exito() throws Exception {
        // GIVEN
        doNothing().when(pacienteService).deletePaciente(id);

        // WHEN & THEN
        mockMvc.perform(delete("/api/paciente/{id}", id))
                .andExpect(status().isOk()); 
    }
}