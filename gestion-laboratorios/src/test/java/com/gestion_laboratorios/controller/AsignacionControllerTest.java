package com.gestion_laboratorios.controller;

import com.gestion_laboratorios.DTO.AsignacionDTO;
import com.gestion_laboratorios.hateoas.AsignacionModelAssembler;
import com.gestion_laboratorios.model.Asignacion;
import com.gestion_laboratorios.model.ResponseWrapper;
import com.gestion_laboratorios.service.AsignacionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AsignacionControllerTest {

    @Mock
    private AsignacionService asignacionService;

    @Mock
    private AsignacionModelAssembler asignacionModelAssembler;

    @InjectMocks
    private AsignacionController asignacionController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllAsignaciones_DeberiaRetornarLista() {
        // GIVEN
        Asignacion a1 = new Asignacion();
        Asignacion a2 = new Asignacion();
        a1.setId(UUID.randomUUID());
        a2.setId(UUID.randomUUID());

        when(asignacionService.getAllAsignaciones()).thenReturn(Arrays.asList(a1, a2));
        when(asignacionModelAssembler.toModel(a1)).thenReturn(EntityModel.of(a1));
        when(asignacionModelAssembler.toModel(a2)).thenReturn(EntityModel.of(a2));

        // WHEN
        ResponseEntity<CollectionModel<EntityModel<Asignacion>>> response = asignacionController.getAllAsignaciones();

        // THEN
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().getContent().size());
        verify(asignacionService, times(1)).getAllAsignaciones();
        verify(asignacionModelAssembler, times(2)).toModel(any(Asignacion.class));
    }

    @Test
    void getAsignacionById_DeberiaRetornarAsignacion() {
        // GIVEN
        UUID id = UUID.randomUUID();
        Asignacion asignacion = new Asignacion();
        asignacion.setId(id);

        when(asignacionService.getAsignacionById(id)).thenReturn(asignacion);
        when(asignacionModelAssembler.toModel(asignacion)).thenReturn(EntityModel.of(asignacion));

        // WHEN
        ResponseEntity<EntityModel<Asignacion>> response = asignacionController.getAsignacionById(id);

        // THEN
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(asignacion, response.getBody().getContent());
        verify(asignacionService, times(1)).getAsignacionById(id);
    }

    @Test
    void getAsignacionesByUserId_DeberiaRetornarLista() {
        // GIVEN
        UUID userId = UUID.randomUUID();
        Asignacion a1 = new Asignacion();
        Asignacion a2 = new Asignacion();

        when(asignacionService.getAsignacionesByUserId(userId)).thenReturn(Arrays.asList(a1, a2));

        // WHEN
        ResponseEntity<List<Asignacion>> response = asignacionController.getAsignacionesByUserId(userId);

        // THEN
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        verify(asignacionService, times(1)).getAsignacionesByUserId(userId);
    }

    @Test
    void createAsignacion_DeberiaCrearAsignacion() {
        // GIVEN
        AsignacionDTO dto = new AsignacionDTO();
        Asignacion created = new Asignacion();
        created.setId(UUID.randomUUID());

        when(asignacionService.crearAsignacion(dto)).thenReturn(created);
        when(asignacionModelAssembler.toModel(created)).thenReturn(EntityModel.of(created));

        // WHEN
        ResponseEntity<EntityModel<Asignacion>> response = asignacionController.createAsignacion(dto);

        // THEN
        assertEquals(201, response.getStatusCodeValue());
        assertEquals(created, response.getBody().getContent());
        verify(asignacionService, times(1)).crearAsignacion(dto);
    }

    @Test
    void updateAsignacion_DeberiaActualizarAsignacion() {
        // GIVEN
        UUID id = UUID.randomUUID();
        AsignacionDTO dto = new AsignacionDTO();
        Asignacion updated = new Asignacion();
        updated.setId(id);

        when(asignacionService.updateAsignacion(id, dto)).thenReturn(updated);
        when(asignacionModelAssembler.toModel(updated)).thenReturn(EntityModel.of(updated));

        // WHEN
        ResponseEntity<EntityModel<Asignacion>> response = asignacionController.updateAsignacion(id, dto);

        // THEN
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(updated, response.getBody().getContent());
        verify(asignacionService, times(1)).updateAsignacion(id, dto);
    }

    @Test
    void deleteAsignacion_DeberiaEliminarAsignacion() {
        // GIVEN
        UUID id = UUID.randomUUID();

        // WHEN
        ResponseEntity<?> response = asignacionController.deleteAsignacion(id);

        // THEN
        assertEquals(200, response.getStatusCodeValue());
        verify(asignacionService, times(1)).deleteAsignacion(id);
        assertTrue(response.getBody() instanceof ResponseWrapper);
    }
}
