package com.gestion_usuarios.service;

import com.gestion_usuarios.model.Area;
import com.gestion_usuarios.repository.AreaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AreaServiceTest {

    @Mock
    private AreaRepository areaRepository;

    @InjectMocks
    private AreaService areaService;

    // --- TEST: getAllAreas ---
    @Test
    void getAllAreas_DeberiaRetornarListaDeAreas() {
        // GIVEN
        Area area1 = new Area();
        area1.setNombre("Recursos Humanos");
        Area area2 = new Area();
        area2.setNombre("Finanzas");

        when(areaRepository.findAll()).thenReturn(Arrays.asList(area1, area2));

        // WHEN
        List<Area> resultado = areaService.getAllAreas();

        // THEN
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(areaRepository, times(1)).findAll();
    }

    // --- TEST: createArea ---
    @Test
    void createArea_DeberiaGuardarYRetornarArea() {
        // GIVEN
        Area areaParaGuardar = new Area();
        areaParaGuardar.setNombre("IT");

        Area areaGuardada = new Area();
        areaGuardada.setId(UUID.randomUUID());
        areaGuardada.setNombre("IT");

        when(areaRepository.save(areaParaGuardar)).thenReturn(areaGuardada);

        // WHEN
        Area resultado = areaService.createArea(areaParaGuardar);

        // THEN
        assertNotNull(resultado);
        assertNotNull(resultado.getId());
        assertEquals("IT", resultado.getNombre());
        verify(areaRepository).save(areaParaGuardar);
    }

    // --- TEST: getAreaById (Exitoso) ---
    @Test
    void getAreaById_DeberiaRetornarArea_CuandoExiste() {
        // GIVEN
        UUID id = UUID.randomUUID();
        Area area = new Area();
        area.setId(id);
        area.setNombre("Marketing");

        when(areaRepository.findById(id)).thenReturn(Optional.of(area));

        // WHEN
        Area resultado = areaService.getAreaById(id);

        // THEN
        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
        assertEquals("Marketing", resultado.getNombre());
    }

    // --- TEST: getAreaById (No Encontrado) ---
    @Test
    void getAreaById_DeberiaLanzarExcepcion_CuandoNoExiste() {
        // GIVEN
        UUID id = UUID.randomUUID();
        when(areaRepository.findById(id)).thenReturn(Optional.empty());

        // WHEN & THEN
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            areaService.getAreaById(id);
        });

        assertEquals("Área no encontrada con ID: " + id, exception.getMessage());
    }

    // --- TEST: updateArea (Exitoso) ---
    @Test
    void updateArea_DeberiaActualizarNombre_CuandoExiste() {
        // GIVEN
        UUID id = UUID.randomUUID();

        // Área existente en BD
        Area areaExistente = new Area();
        areaExistente.setId(id);
        areaExistente.setNombre("Nombre Viejo");

        // Datos nuevos para actualizar
        Area areaDetalles = new Area();
        areaDetalles.setNombre("Nombre Nuevo");

        when(areaRepository.findById(id)).thenReturn(Optional.of(areaExistente));
        // Simulamos que al guardar retorna el mismo objeto ya modificado
        when(areaRepository.save(areaExistente)).thenReturn(areaExistente);

        // WHEN
        Area resultado = areaService.updateArea(id, areaDetalles);

        // THEN
        assertNotNull(resultado);
        assertEquals("Nombre Nuevo", resultado.getNombre()); // Verificamos que cambió
        verify(areaRepository).save(areaExistente);
    }

    // --- TEST: updateArea (No Encontrado) ---
    @Test
    void updateArea_DeberiaLanzarExcepcion_CuandoNoExiste() {
        // GIVEN
        UUID id = UUID.randomUUID();
        Area areaDetalles = new Area();
        areaDetalles.setNombre("Intento Update");

        when(areaRepository.findById(id)).thenReturn(Optional.empty());

        // WHEN & THEN
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            areaService.updateArea(id, areaDetalles);
        });

        assertEquals("Área no encontrada con ID: " + id, exception.getMessage());

        // Importante: verificar que NUNCA se llamó a save
        verify(areaRepository, never()).save(any());
    }

    // --- TEST: deleteArea ---
    @Test
    void deleteArea_DeberiaLlamarDeleteById() {
        // GIVEN
        UUID id = UUID.randomUUID();

        // En tu servicio, deleteArea no verifica si existe (void), solo manda borrar.
        // Por lo tanto, no necesitamos mockear findById, solo verificamos la llamada.

        // WHEN
        areaService.deleteArea(id);

        // THEN
        verify(areaRepository, times(1)).deleteById(id);
    }
}