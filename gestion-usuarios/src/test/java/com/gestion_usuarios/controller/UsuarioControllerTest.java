package com.gestion_usuarios.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestion_usuarios.DTO.UsuarioDTO;
import com.gestion_usuarios.exception.ResourceNotFoundException;
import com.gestion_usuarios.hateoas.UsuarioModelAssembler;
import com.gestion_usuarios.mapper.UsuarioMapper;
import com.gestion_usuarios.model.Usuario;
import com.gestion_usuarios.service.CustomUserDetailsService;
import com.gestion_usuarios.service.UsuarioService;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
@AutoConfigureMockMvc(addFilters = false) // Desactiva filtros de seguridad (Login/JWT)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // --- Mocks de Lógica de Negocio ---
    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private UsuarioMapper usuarioMapper;

    @MockBean
    private UsuarioModelAssembler usuarioModelAssembler;

    // --- Mocks de Infraestructura (Vitales para que SecurityConfig no falle) ---
    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    private Usuario usuario;
    private UsuarioDTO usuarioDTO;
    private UUID id;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();

        usuario = new Usuario();
        usuario.setId(id);
        usuario.setUserName("jdoe");
        usuario.setNombre("John");
        usuario.setApellido("Doe");
        usuario.setEmail("jdoe@mail.com");

        usuarioDTO = new UsuarioDTO();
        usuarioDTO.setUserName("jdoe");
        usuarioDTO.setNombre("John");
        // Agrega más campos si @Valid en tu DTO lo requiere
    }

    // --- TEST: GET ALL ---
    @Test
    void getAllUsuarios_Exito() throws Exception {
        // GIVEN
        List<Usuario> lista = List.of(usuario);
        when(usuarioService.getAllUsuarios()).thenReturn(lista);
        // Mock de HATEOAS
        when(usuarioModelAssembler.toModel(any(Usuario.class))).thenReturn(EntityModel.of(usuario));

        // WHEN & THEN
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.usuarioList", hasSize(1)))
                .andExpect(jsonPath("$._embedded.usuarioList[0].userName", is("jdoe")));
    }

    @Test
    void getAllUsuarios_Vacio_LanzaExcepcion() throws Exception {
        // GIVEN
        when(usuarioService.getAllUsuarios()).thenReturn(Collections.emptyList());

        // WHEN & THEN
        // Tu controller lanza explícitamente ResourceNotFoundException si la lista está
        // vacía
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException));
    }

    // --- TEST: GET BY ID ---
    @Test
    void getUsuarioById_Exito() throws Exception {
        // GIVEN
        when(usuarioService.getUsuarioById(id)).thenReturn(usuario);
        when(usuarioModelAssembler.toModel(usuario)).thenReturn(EntityModel.of(usuario));

        // WHEN & THEN
        mockMvc.perform(get("/api/usuarios/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName", is("jdoe")));
    }

    @Test
    void getUsuarioById_NoEncontrado() throws Exception {
        // GIVEN
        when(usuarioService.getUsuarioById(id)).thenThrow(new ResourceNotFoundException("Usuario no encontrado"));

        // WHEN & THEN
        mockMvc.perform(get("/api/usuarios/{id}", id))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException));
    }

    // --- TEST: POST (CREATE) ---
    @Test
    void createUsuario_Exito() throws Exception {
        // GIVEN
        // 1. Controller llama a Mapper
        when(usuarioMapper.toEntity(any(UsuarioDTO.class))).thenReturn(usuario);
        // 2. Controller llama a Service
        when(usuarioService.createUsuario(any(Usuario.class))).thenReturn(usuario);
        // 3. Controller llama a Assembler
        when(usuarioModelAssembler.toModel(any(Usuario.class))).thenReturn(EntityModel.of(usuario));

        // WHEN & THEN
        mockMvc.perform(post("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioDTO)))
                .andExpect(status().isCreated()) // 201 Created
                .andExpect(jsonPath("$.userName", is("jdoe")));
    }

    @Test
    void createUsuario_Invalido_BadRequest() throws Exception {
        // GIVEN: Body vacío o inválido
        mockMvc.perform(post("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    // --- TEST: PUT (UPDATE) ---
    @Test
    void updateUsuario_Exito() throws Exception {
        // GIVEN
        Usuario actualizado = new Usuario();
        actualizado.setId(id);
        actualizado.setUserName("jdoe_updated");

        UsuarioDTO dtoUpdate = new UsuarioDTO();
        dtoUpdate.setUserName("jdoe_updated");

        // Mocks en orden de llamada
        when(usuarioMapper.toEntity(any(UsuarioDTO.class))).thenReturn(actualizado);
        when(usuarioService.updateUsuario(eq(id), any(Usuario.class))).thenReturn(actualizado);
        when(usuarioModelAssembler.toModel(actualizado)).thenReturn(EntityModel.of(actualizado));

        // WHEN & THEN
        mockMvc.perform(put("/api/usuarios/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName", is("jdoe_updated")));
    }

    // --- TEST: DELETE ---
    @Test
    void deleteUsuario_Exito() throws Exception {
        // GIVEN
        doNothing().when(usuarioService).deleteUsuario(id);

        // WHEN & THEN
        mockMvc.perform(delete("/api/usuarios/{id}", id))
                .andExpect(status().isOk());
    }
}