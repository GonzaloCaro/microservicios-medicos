package com.gestion_laboratorios.controller;

import com.gestion_laboratorios.DTO.LaboratorioDTO;
import com.gestion_laboratorios.hateoas.LaboratorioModelAssembler;
import com.gestion_laboratorios.mapper.LaboratorioMapper;
import com.gestion_laboratorios.model.Laboratorio;
import com.gestion_laboratorios.model.ResponseWrapper;
import com.gestion_laboratorios.service.LaboratorioService;
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

class LaboratorioControllerTest {

    @Mock
    private LaboratorioService laboratorioService;

    @Mock
    private LaboratorioMapper laboratorioMapper;

    @Mock
    private LaboratorioModelAssembler laboratorioModelAssembler;

    @InjectMocks
    private LaboratorioController laboratorioController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllLaboratorios_DeberiaRetornarLista() {
        // GIVEN
        Laboratorio lab1 = new Laboratorio();
        lab1.setId(UUID.randomUUID());
        Laboratorio lab2 = new Laboratorio();
        lab2.setId(UUID.randomUUID());

        when(laboratorioService.getAllLaboratorios()).thenReturn(Arrays.asList(lab1, lab2));
        when(laboratorioModelAssembler.toModel(lab1)).thenReturn(EntityModel.of(lab1));
        when(laboratorioModelAssembler.toModel(lab2)).thenReturn(EntityModel.of(lab2));

        // WHEN
        ResponseEntity<CollectionModel<EntityModel<Laboratorio>>> response = laboratorioController.getAllLaboratorios();

        // THEN
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().getContent().size());
        verify(laboratorioService, times(1)).getAllLaboratorios();
        verify(laboratorioModelAssembler, times(2)).toModel(any(Laboratorio.class));
    }

    @Test
    void getLaboratorioById_DeberiaRetornarLaboratorio() {
        // GIVEN
        UUID id = UUID.randomUUID();
        Laboratorio lab = new Laboratorio();
        lab.setId(id);

        when(laboratorioService.getLaboratorioById(id)).thenReturn(lab);
        when(laboratorioModelAssembler.toModel(lab)).thenReturn(EntityModel.of(lab));

        // WHEN
        ResponseEntity<EntityModel<Laboratorio>> response = laboratorioController.getLaboratorioById(id);

        // THEN
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(lab, response.getBody().getContent());
        verify(laboratorioService, times(1)).getLaboratorioById(id);
        verify(laboratorioModelAssembler, times(1)).toModel(lab);
    }

    @Test
    void createLaboratorio_DeberiaCrearLaboratorio() {
        // GIVEN
        LaboratorioDTO dto = new LaboratorioDTO();
        Laboratorio labEntity = new Laboratorio();
        labEntity.setId(UUID.randomUUID());

        when(laboratorioMapper.toEntity(dto)).thenReturn(labEntity);
        when(laboratorioService.createLaboratorio(labEntity)).thenReturn(labEntity);
        when(laboratorioModelAssembler.toModel(labEntity)).thenReturn(EntityModel.of(labEntity));

        // WHEN
        ResponseEntity<EntityModel<Laboratorio>> response = laboratorioController.createLaboratorio(dto);

        // THEN
        assertEquals(201, response.getStatusCodeValue());
        assertEquals(labEntity, response.getBody().getContent());
        verify(laboratorioService, times(1)).createLaboratorio(labEntity);
    }

    @Test
    void updateLaboratorio_DeberiaActualizarLaboratorio() {
        // GIVEN
        UUID id = UUID.randomUUID();
        LaboratorioDTO dto = new LaboratorioDTO();
        Laboratorio labEntity = new Laboratorio();
        labEntity.setId(id);

        when(laboratorioMapper.toEntity(dto)).thenReturn(labEntity);
        when(laboratorioService.updateLaboratorio(id, labEntity)).thenReturn(labEntity);
        when(laboratorioModelAssembler.toModel(labEntity)).thenReturn(EntityModel.of(labEntity));

        // WHEN
        ResponseEntity<EntityModel<Laboratorio>> response = laboratorioController.updateLaboratorio(id, dto);

        // THEN
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(labEntity, response.getBody().getContent());
        verify(laboratorioService, times(1)).updateLaboratorio(id, labEntity);
    }

    @Test
    void deleteLaboratorio_DeberiaEliminarLaboratorio() {
        // GIVEN
        UUID id = UUID.randomUUID();

        // WHEN
        ResponseEntity<?> response = laboratorioController.deleteLaboratorio(id);

        // THEN
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof ResponseWrapper);
        verify(laboratorioService, times(1)).deleteLaboratorio(id);
    }
}
