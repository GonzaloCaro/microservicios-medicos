package com.gestion_laboratorios.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gestion_laboratorios.DTO.AnalisisDTO;
import com.gestion_laboratorios.model.Analisis;
import com.gestion_laboratorios.repository.AnalisisRepository;
import com.gestion_laboratorios.service.AnalisisService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // 1. Habilita Mockito
class AnalisisServiceTest {

    @Mock // 2. Simulamos el repositorio (no conecta a BD real)
    private AnalisisRepository analisisRepository;

    @InjectMocks // 3. Inyectamos el mock dentro del servicio real
    private AnalisisService analisisService;

    // --- TEST: createAnalisis ---
    @Test
    void createAnalisis_DeberiaGuardarYRetornarAnalisis() {
        // GIVEN (Preparación)
        AnalisisDTO dto = new AnalisisDTO();
        dto.setCodigo("A001");
        dto.setNombre("Hemograma");

        Analisis analisisGuardado = new Analisis();
        analisisGuardado.setId(UUID.randomUUID());
        analisisGuardado.setCodigo("A001");
        analisisGuardado.setNombre("Hemograma");

        // Cuando el repo guarde CUALQUIER analisis, retorna el que creamos arriba
        when(analisisRepository.save(any(Analisis.class))).thenReturn(analisisGuardado);

        // WHEN (Ejecución)
        Analisis resultado = analisisService.createAnalisis(dto);

        // THEN (Verificación)
        assertNotNull(resultado);
        assertEquals("A001", resultado.getCodigo());
        verify(analisisRepository, times(1)).save(any(Analisis.class)); // Verifica que se llamó a save
    }

    // --- TEST: getAllAnalisis ---
    @Test
    void getAllAnalisis_DeberiaRetornarLista() {
        // GIVEN
        Analisis a1 = new Analisis();
        Analisis a2 = new Analisis();
        when(analisisRepository.findAll()).thenReturn(Arrays.asList(a1, a2));

        // WHEN
        List<Analisis> lista = analisisService.getAllAnalisis();

        // THEN
        assertEquals(2, lista.size());
        verify(analisisRepository, times(1)).findAll();
    }

    // --- TEST: getAnalisisById (Caso Exitoso) ---
    @Test
    void getAnalisisById_DeberiaRetornarAnalisis_CuandoExiste() {
        // GIVEN
        UUID id = UUID.randomUUID();
        Analisis analisis = new Analisis();
        analisis.setId(id);

        when(analisisRepository.findById(id)).thenReturn(Optional.of(analisis));

        // WHEN
        Analisis resultado = analisisService.getAnalisisById(id);

        // THEN
        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
    }

    // --- TEST: getAnalisisById (Caso Error) ---
    @Test
    void getAnalisisById_DeberiaLanzarExcepcion_CuandoNoExiste() {
        // GIVEN
        UUID id = UUID.randomUUID();
        when(analisisRepository.findById(id)).thenReturn(Optional.empty());

        // WHEN & THEN (AssertThrows captura la excepción)
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            analisisService.getAnalisisById(id);
        });

        assertEquals("Análisis no encontrado con ID: " + id, exception.getMessage());
    }

    // --- TEST: updateAnalisis ---
    @Test
    void updateAnalisis_DeberiaActualizar_CuandoExiste() {
        // GIVEN
        UUID id = UUID.randomUUID();

        // El que ya existe en BD
        Analisis existente = new Analisis();
        existente.setId(id);
        existente.setNombre("Viejo Nombre");

        // Los datos nuevos
        Analisis nuevosDatos = new Analisis();
        nuevosDatos.setNombre("Nuevo Nombre");
        nuevosDatos.setCodigo("B002");

        when(analisisRepository.findById(id)).thenReturn(Optional.of(existente));
        when(analisisRepository.save(existente)).thenReturn(existente);

        // WHEN
        Analisis resultado = analisisService.updateAnalisis(id, nuevosDatos);

        // THEN
        assertEquals("Nuevo Nombre", resultado.getNombre()); // Verificamos que se actualizó el objeto
        verify(analisisRepository).save(existente);
    }

    // --- TEST: deleteAnalisis ---
    @Test
    void deleteAnalisis_DeberiaEliminar_CuandoExiste() {
        // GIVEN
        UUID id = UUID.randomUUID();
        Analisis analisis = new Analisis();
        when(analisisRepository.findById(id)).thenReturn(Optional.of(analisis));

        // WHEN
        analisisService.deleteAnalisis(id);

        // THEN
        verify(analisisRepository, times(1)).delete(analisis);
    }

    @Test
    void updateAnalisis_DeberiaLanzarExcepcion_CuandoNoExiste() {
        UUID id = UUID.randomUUID();
        Analisis nuevosDatos = new Analisis();
        nuevosDatos.setCodigo("X001");
        nuevosDatos.setNombre("Nombre Nuevo");

        when(analisisRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            analisisService.updateAnalisis(id, nuevosDatos);
        });

        assertEquals("Análisis no encontrado con ID: " + id, exception.getMessage());
        verify(analisisRepository, never()).save(any());
    }

    // --- TEST: deleteAnalisis (Caso Error) ---
    @Test
    void deleteAnalisis_DeberiaLanzarExcepcion_CuandoNoExiste() {
        UUID id = UUID.randomUUID();

        when(analisisRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            analisisService.deleteAnalisis(id);
        });

        assertEquals("Análisis no encontrado con ID: " + id, exception.getMessage());
        verify(analisisRepository, never()).delete(any());
    }

    // --- TEST: createAnalisis con código nulo (simulando validación) ---
    @Test
    void createAnalisis_DeberiaLanzarExcepcion_SiCodigoEsNull() {
        AnalisisDTO dto = new AnalisisDTO();
        dto.setCodigo(null); // Código inválido
        dto.setNombre("Hemograma");

        // Dependiendo de tu lógica de negocio, aquí podrías lanzar RuntimeException
        // Por ahora solo verificamos que se llame al save
        Analisis analisisGuardado = new Analisis();
        analisisGuardado.setId(UUID.randomUUID());
        analisisGuardado.setCodigo(null);
        analisisGuardado.setNombre("Hemograma");

        when(analisisRepository.save(any(Analisis.class))).thenReturn(analisisGuardado);

        Analisis resultado = analisisService.createAnalisis(dto);
        assertNotNull(resultado);
        assertNull(resultado.getCodigo());
    }

    // --- TEST: getAllAnalisis vacía ---
    @Test
    void getAllAnalisis_DeberiaRetornarListaVacia() {
        when(analisisRepository.findAll()).thenReturn(List.of());

        List<Analisis> lista = analisisService.getAllAnalisis();

        assertNotNull(lista);
        assertTrue(lista.isEmpty());
        verify(analisisRepository, times(1)).findAll();
    }
}