package com.gestion_laboratorios.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gestion_laboratorios.DTO.AsignacionDTO;
import com.gestion_laboratorios.model.Asignacion;
import com.gestion_laboratorios.model.Paciente;
import com.gestion_laboratorios.repository.AsignacionRepository;
import com.gestion_laboratorios.repository.PacienteRepository;
import com.gestion_laboratorios.service.AsignacionService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AsignacionServiceTest {

    @Mock
    private AsignacionRepository asignacionRepository;

    @Mock
    private PacienteRepository pacienteRepository;

    @InjectMocks
    private AsignacionService asignacionService;

    // --- 1. Test getAllAsignaciones ---
    @Test
    void getAllAsignaciones_DeberiaRetornarLista() {
        when(asignacionRepository.findAll()).thenReturn(Arrays.asList(new Asignacion(), new Asignacion()));

        List<Asignacion> resultado = asignacionService.getAllAsignaciones();

        assertEquals(2, resultado.size());
        verify(asignacionRepository).findAll();
    }

    // --- 2. Test getAsignacionById ---
    @Test
    void getAsignacionById_Exito() {
        UUID id = UUID.randomUUID();
        Asignacion asig = new Asignacion();
        asig.setId(id);

        when(asignacionRepository.findById(id)).thenReturn(Optional.of(asig));

        Asignacion resultado = asignacionService.getAsignacionById(id);
        assertEquals(id, resultado.getId());
    }

    @Test
    void getAsignacionById_NoEncontrado() {
        UUID id = UUID.randomUUID();
        when(asignacionRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> asignacionService.getAsignacionById(id));
    }

    // --- 3. Test getAsignacionesByUserId ---
    @Test
    void getAsignacionesByUserId_DeberiaRetornarLista() {
        UUID userId = UUID.randomUUID();
        when(asignacionRepository.findByUsuarioId(userId)).thenReturn(Arrays.asList(new Asignacion()));

        List<Asignacion> resultado = asignacionService.getAsignacionesByUserId(userId);

        assertFalse(resultado.isEmpty());
        verify(asignacionRepository).findByUsuarioId(userId);
    }

    // ==========================================
    // TESTS COMPLEJOS: crearAsignacion
    // ==========================================

    // Caso A: El DTO trae un ID de Paciente (El paciente ya existe en BD)
    @Test
    void crearAsignacion_ConPacienteId_DeberiaUsarPacienteExistente() {
        // GIVEN
        UUID pacienteId = UUID.randomUUID();
        AsignacionDTO dto = new AsignacionDTO();
        dto.setPacienteId(pacienteId);
        dto.setLaboratorioId(UUID.randomUUID());

        Paciente pacienteExistente = new Paciente();
        pacienteExistente.setId(pacienteId);

        when(pacienteRepository.findById(pacienteId)).thenReturn(Optional.of(pacienteExistente));
        when(asignacionRepository.save(any(Asignacion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // WHEN
        Asignacion resultado = asignacionService.crearAsignacion(dto);

        // THEN
        assertNotNull(resultado);
        assertEquals(pacienteId, resultado.getPaciente().getId());

        // Verificamos que NO buscó por RUT ni intentó guardar un paciente nuevo
        verify(pacienteRepository, never()).findByRut(anyString());
        verify(pacienteRepository, never()).save(any(Paciente.class));
    }

    // Caso B: El DTO NO trae ID, pero trae RUT y el Paciente YA existe
    @Test
    void crearAsignacion_SinId_ConRutExistente_DeberiaBuscarYUsarPaciente() {
        // GIVEN
        AsignacionDTO dto = new AsignacionDTO();
        dto.setPacienteId(null); // No ID
        dto.setRut("12345678"); // Sí RUT

        Paciente pacienteEncontrado = new Paciente();
        pacienteEncontrado.setId(UUID.randomUUID());
        pacienteEncontrado.setRut("12345678");

        when(pacienteRepository.findByRut("12345678")).thenReturn(Optional.of(pacienteEncontrado));
        when(asignacionRepository.save(any(Asignacion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // WHEN
        Asignacion resultado = asignacionService.crearAsignacion(dto);

        // THEN
        assertEquals("12345678", resultado.getPaciente().getRut());

        // Verificamos que buscó por RUT pero NO guardó un paciente nuevo
        verify(pacienteRepository).findByRut("12345678");
        verify(pacienteRepository, never()).save(any(Paciente.class));
    }

    // Caso C: El DTO NO trae ID, y el RUT NO existe (Debería crear Paciente Nuevo)
    @Test
    void crearAsignacion_PacienteNuevo_DeberiaCrearPacienteYAsignacion() {
        // GIVEN
        AsignacionDTO dto = new AsignacionDTO();
        dto.setRut("99999999");
        dto.setNombrePaciente("Nuevo");

        // Mock: No encuentra el paciente por RUT
        when(pacienteRepository.findByRut("99999999")).thenReturn(Optional.empty());

        // Mock: Guarda el nuevo paciente y devuelve uno con ID generado
        Paciente pacienteNuevoGuardado = new Paciente();
        pacienteNuevoGuardado.setId(UUID.randomUUID());
        pacienteNuevoGuardado.setRut("99999999");

        when(pacienteRepository.save(any(Paciente.class))).thenReturn(pacienteNuevoGuardado);
        when(asignacionRepository.save(any(Asignacion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // WHEN
        Asignacion resultado = asignacionService.crearAsignacion(dto);

        // THEN
        assertNotNull(resultado.getPaciente());
        assertEquals("99999999", resultado.getPaciente().getRut());

        // IMPORTANTE: Verificar que sí llamó a guardar el paciente
        verify(pacienteRepository).save(any(Paciente.class));
        verify(asignacionRepository).save(any(Asignacion.class));
    }

    // ==========================================
    // TESTS: updateAsignacion
    // ==========================================

    @Test
    void updateAsignacion_DeberiaActualizarTodosLosCampos_CuandoSonNoNulos() {
        // GIVEN
        UUID id = UUID.randomUUID();

        // Datos viejos en BD
        Asignacion existente = new Asignacion();
        existente.setId(id);
        existente.setLaboratorioId(UUID.randomUUID()); // Valor viejo A
        existente.setAnalisisId(UUID.randomUUID()); // Valor viejo B
        existente.setDetalle("Detalle viejo"); // Valor viejo C

        // Datos nuevos en el DTO (Todos llenos)
        AsignacionDTO dto = new AsignacionDTO();
        UUID nuevoLabId = UUID.randomUUID();
        UUID nuevoAnalisisId = UUID.randomUUID();

        dto.setLaboratorioId(nuevoLabId); // Nuevo valor A
        dto.setAnalisisId(nuevoAnalisisId); // Nuevo valor B
        dto.setDetalle("Detalle nuevo"); // Nuevo valor C

        when(asignacionRepository.findById(id)).thenReturn(Optional.of(existente));
        when(asignacionRepository.save(existente)).thenReturn(existente);

        // WHEN
        Asignacion resultado = asignacionService.updateAsignacion(id, dto);

        // THEN
        // Verificamos que todo cambió (entró a todos los IF)
        assertEquals(nuevoLabId, resultado.getLaboratorioId());
        assertEquals(nuevoAnalisisId, resultado.getAnalisisId());
        assertEquals("Detalle nuevo", resultado.getDetalle());
    }

    @Test
    void updateAsignacion_DeberiaIgnorarCamposNulos_Y_MantenerValoresAntiguos() {
        // GIVEN
        UUID id = UUID.randomUUID();
        UUID labIdOriginal = UUID.randomUUID();
        UUID analisisIdOriginal = UUID.randomUUID();

        // Datos viejos en BD
        Asignacion existente = new Asignacion();
        existente.setId(id);
        existente.setLaboratorioId(labIdOriginal);
        existente.setAnalisisId(analisisIdOriginal);
        existente.setDetalle("Detalle original");

        AsignacionDTO dto = new AsignacionDTO();
        dto.setLaboratorioId(null);
        dto.setAnalisisId(null);
        dto.setDetalle(null);

        when(asignacionRepository.findById(id)).thenReturn(Optional.of(existente));
        when(asignacionRepository.save(existente)).thenReturn(existente);

        // WHEN
        Asignacion resultado = asignacionService.updateAsignacion(id, dto);

        // THEN
        // Verificamos que NADA cambió (No entró a ningún IF)
        assertEquals(labIdOriginal, resultado.getLaboratorioId());
        assertEquals(analisisIdOriginal, resultado.getAnalisisId());
        assertEquals("Detalle original", resultado.getDetalle());
    }

    // ==========================================
    // TESTS: deleteAsignacion
    // ==========================================

    @Test
    void deleteAsignacion_Exito() {
        UUID id = UUID.randomUUID();
        // Simulamos que existe
        when(asignacionRepository.existsById(id)).thenReturn(true);

        // WHEN
        asignacionService.deleteAsignacion(id);

        // THEN
        verify(asignacionRepository).deleteById(id);
    }

    @Test
    void deleteAsignacion_NoExiste_LanzaExcepcion() {
        UUID id = UUID.randomUUID();
        // Simulamos que NO existe
        when(asignacionRepository.existsById(id)).thenReturn(false);

        // WHEN & THEN
        RuntimeException ex = assertThrows(RuntimeException.class, () -> asignacionService.deleteAsignacion(id));

        assertEquals("No se puede eliminar, asignacion no encontrada", ex.getMessage());
        verify(asignacionRepository, never()).deleteById(any());
    }
}