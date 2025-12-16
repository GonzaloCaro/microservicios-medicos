package com.gestion_usuarios.service;

import com.gestion_usuarios.model.Rol;
import com.gestion_usuarios.repository.RolRepository;
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
class RolServiceTest {

    @Mock
    private RolRepository rolRepository;

    @InjectMocks
    private RolService rolService;

    // --- TEST: getAllRoles ---
    @Test
    void getAllRoles_DeberiaRetornarListaDeRoles() {
        // GIVEN
        Rol rol1 = new Rol();
        rol1.setNombre("ROLE_ADMIN");
        Rol rol2 = new Rol();
        rol2.setNombre("ROLE_USER");

        when(rolRepository.findAll()).thenReturn(Arrays.asList(rol1, rol2));

        // WHEN
        List<Rol> resultado = rolService.getAllRoles();

        // THEN
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(rolRepository, times(1)).findAll();
    }

    // --- TEST: createRol ---
    @Test
    void createRol_DeberiaGuardarYRetornarRol() {
        // GIVEN
        Rol rolNuevo = new Rol();
        rolNuevo.setNombre("ROLE_MANAGER");

        Rol rolGuardado = new Rol();
        rolGuardado.setId(UUID.randomUUID());
        rolGuardado.setNombre("ROLE_MANAGER");

        when(rolRepository.save(rolNuevo)).thenReturn(rolGuardado);

        // WHEN
        Rol resultado = rolService.createRol(rolNuevo);

        // THEN
        assertNotNull(resultado);
        assertNotNull(resultado.getId());
        assertEquals("ROLE_MANAGER", resultado.getNombre());
        verify(rolRepository).save(rolNuevo);
    }

    // --- TEST: getRolById (Exitoso) ---
    @Test
    void getRolById_DeberiaRetornarRol_CuandoExiste() {
        // GIVEN
        UUID id = UUID.randomUUID();
        Rol rol = new Rol();
        rol.setId(id);
        rol.setNombre("ROLE_TEST");

        when(rolRepository.findById(id)).thenReturn(Optional.of(rol));

        // WHEN
        Rol resultado = rolService.getRolById(id);

        // THEN
        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
        assertEquals("ROLE_TEST", resultado.getNombre());
    }

    // --- TEST: getRolById (No Encontrado) ---
    @Test
    void getRolById_DeberiaLanzarExcepcion_CuandoNoExiste() {
        // GIVEN
        UUID id = UUID.randomUUID();
        when(rolRepository.findById(id)).thenReturn(Optional.empty());

        // WHEN & THEN
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            rolService.getRolById(id);
        });

        assertEquals("Rol no encontrado con ID: " + id, exception.getMessage());
    }

    // --- TEST: updateRol (Exitoso) ---
    @Test
    void updateRol_DeberiaActualizarNombre_CuandoExiste() {
        // GIVEN
        UUID id = UUID.randomUUID();

        // Rol en base de datos
        Rol rolExistente = new Rol();
        rolExistente.setId(id);
        rolExistente.setNombre("ROLE_OLD");

        // Datos nuevos
        Rol rolDetalles = new Rol();
        rolDetalles.setNombre("ROLE_NEW");

        when(rolRepository.findById(id)).thenReturn(Optional.of(rolExistente));
        when(rolRepository.save(rolExistente)).thenReturn(rolExistente);

        // WHEN
        Rol resultado = rolService.updateRol(id, rolDetalles);

        // THEN
        assertNotNull(resultado);
        assertEquals("ROLE_NEW", resultado.getNombre()); // Verificamos que el setNombre funcionó
        verify(rolRepository).save(rolExistente);
    }

    // --- TEST: updateRol (No Encontrado) ---
    @Test
    void updateRol_DeberiaLanzarExcepcion_CuandoNoExiste() {
        // GIVEN
        UUID id = UUID.randomUUID();
        Rol rolDetalles = new Rol();
        rolDetalles.setNombre("ROLE_UPDATE");

        when(rolRepository.findById(id)).thenReturn(Optional.empty());

        // WHEN & THEN
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            rolService.updateRol(id, rolDetalles);
        });

        assertEquals("Rol no encontrado con ID: " + id, exception.getMessage());
        // Verificamos seguridad: nunca se debe guardar si no se encontró
        verify(rolRepository, never()).save(any());
    }

    // --- TEST: deleteRol ---
    @Test
    void deleteRol_DeberiaLlamarDeleteById() {
        // GIVEN
        UUID id = UUID.randomUUID();
        // deleteRol retorna void y no valida existencia en tu código actual

        // WHEN
        rolService.deleteRol(id);

        // THEN
        verify(rolRepository, times(1)).deleteById(id);
    }
}