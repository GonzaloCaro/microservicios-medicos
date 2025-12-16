package com.gestion_laboratorios.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gestion_laboratorios.model.Laboratorio;
import com.gestion_laboratorios.repository.LaboratorioRepository;
import com.gestion_laboratorios.service.LaboratorioService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LaboratorioServiceTest {

    @Mock
    private LaboratorioRepository laboratorioRepository;

    @InjectMocks
    private LaboratorioService laboratorioService;

    // --- 1. Test para getAllLaboratorios ---
    @Test
    void getAllLaboratorios_DeberiaRetornarLista() {
        // GIVEN
        Laboratorio lab1 = new Laboratorio();
        lab1.setNombre("Lab Central");
        Laboratorio lab2 = new Laboratorio();
        lab2.setNombre("Lab Norte");

        when(laboratorioRepository.findAll()).thenReturn(Arrays.asList(lab1, lab2));

        // WHEN
        List<Laboratorio> resultado = laboratorioService.getAllLaboratorios();

        // THEN
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(laboratorioRepository, times(1)).findAll();
    }

    // --- 2. Test para createLaboratorio ---
    @Test
    void createLaboratorio_DeberiaGuardarYRetornarLaboratorio() {
        // GIVEN
        Laboratorio laboratorioNuevo = new Laboratorio();
        laboratorioNuevo.setNombre("Lab Sur");
        laboratorioNuevo.setUbicacion("Santiago");

        when(laboratorioRepository.save(any(Laboratorio.class))).thenReturn(laboratorioNuevo);

        // WHEN
        Laboratorio resultado = laboratorioService.createLaboratorio(laboratorioNuevo);

        // THEN
        assertNotNull(resultado);
        assertEquals("Lab Sur", resultado.getNombre());
        verify(laboratorioRepository).save(laboratorioNuevo);
    }

    // --- 3. Test para getLaboratorioById (Exitoso) ---
    @Test
    void getLaboratorioById_DeberiaRetornarObjeto_CuandoExiste() {
        // GIVEN
        UUID id = UUID.randomUUID();
        Laboratorio lab = new Laboratorio();
        lab.setId(id);

        when(laboratorioRepository.findById(id)).thenReturn(Optional.of(lab));

        // WHEN
        Laboratorio resultado = laboratorioService.getLaboratorioById(id);

        // THEN
        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
    }

    // --- 4. Test para getLaboratorioById (No encontrado) ---
    @Test
    void getLaboratorioById_DeberiaLanzarExcepcion_CuandoNoExiste() {
        // GIVEN
        UUID id = UUID.randomUUID();
        when(laboratorioRepository.findById(id)).thenReturn(Optional.empty());

        // WHEN & THEN
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            laboratorioService.getLaboratorioById(id);
        });

        assertEquals("Laboratorio no encontrado con ID: " + id, ex.getMessage());
    }

    // --- 5. Test para updateLaboratorio (Exitoso) ---
    @Test
    void updateLaboratorio_DeberiaActualizar_CuandoExiste() {
        // GIVEN
        UUID id = UUID.randomUUID();

        // Datos existentes en BD
        Laboratorio existente = new Laboratorio();
        existente.setId(id);
        existente.setNombre("Nombre Viejo");
        existente.setUbicacion("Ubicacion Vieja");

        // Datos nuevos que vienen del request
        Laboratorio nuevosDatos = new Laboratorio();
        nuevosDatos.setNombre("Nombre Nuevo");
        nuevosDatos.setUbicacion("Ubicacion Nueva");

        when(laboratorioRepository.findById(id)).thenReturn(Optional.of(existente));
        when(laboratorioRepository.save(existente)).thenReturn(existente);

        // WHEN
        Laboratorio resultado = laboratorioService.updateLaboratorio(id, nuevosDatos);

        // THEN
        // Verificamos que el objeto existente haya mutado sus valores
        assertEquals("Nombre Nuevo", resultado.getNombre());
        assertEquals("Ubicacion Nueva", resultado.getUbicacion());
        verify(laboratorioRepository).save(existente);
    }

    // --- 6. Test para updateLaboratorio (No encontrado) ---
    @Test
    void updateLaboratorio_DeberiaLanzarExcepcion_CuandoNoExiste() {
        // GIVEN
        UUID id = UUID.randomUUID();
        Laboratorio nuevosDatos = new Laboratorio();

        when(laboratorioRepository.findById(id)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(RuntimeException.class, () -> {
            laboratorioService.updateLaboratorio(id, nuevosDatos);
        });

        // Importante: verificar que NUNCA se llamó a save si falló el find
        verify(laboratorioRepository, never()).save(any());
    }

    // --- 7. Test para deleteLaboratorio ---
    @Test
    void deleteLaboratorio_DeberiaLlamarDeleteById() {
        // GIVEN
        UUID id = UUID.randomUUID();
        // Nota: En tu código original usas deleteById directo, sin validar si existe
        // antes.
        // Por lo tanto, no necesitamos mockear findById, solo verificar la llamada a
        // delete.

        // WHEN
        laboratorioService.deleteLaboratorio(id);

        // THEN
        verify(laboratorioRepository, times(1)).deleteById(id);
    }
}