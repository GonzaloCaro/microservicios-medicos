package com.gestion_laboratorios.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gestion_laboratorios.DTO.PacienteDTO;
import com.gestion_laboratorios.model.Paciente;
import com.gestion_laboratorios.repository.PacienteRepository;
import com.gestion_laboratorios.service.PacienteService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PacienteServiceTest {

    @Mock
    private PacienteRepository pacienteRepository;

    @InjectMocks
    private PacienteService pacienteService;

    // --- 1. Test getAllPacientes ---
    @Test
    void getAllPacientes_DeberiaRetornarLista() {
        // GIVEN
        Paciente p1 = new Paciente();
        p1.setNombrePaciente("Juan");
        Paciente p2 = new Paciente();
        p2.setNombrePaciente("Maria");

        when(pacienteRepository.findAll()).thenReturn(Arrays.asList(p1, p2));

        // WHEN
        List<Paciente> resultado = pacienteService.getAllPacientes();

        // THEN
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(pacienteRepository, times(1)).findAll();
    }

    // --- 2. Test createPaciente ---
    @Test
    void createPaciente_DeberiaMapearYGuardarPaciente() {
        // GIVEN
        PacienteDTO dto = new PacienteDTO();
        dto.setRut("12345678");
        dto.setDv("K");
        dto.setNombrePaciente("Pedro");
        dto.setApellidoPaciente("Perez");
        dto.setEdad(30);
        dto.setTelefono("987654321");

        // Simulamos que al guardar, el repositorio devuelve un paciente con ID
        Paciente pacienteGuardado = new Paciente();
        pacienteGuardado.setId(UUID.randomUUID());
        pacienteGuardado.setRut("12345678");
        pacienteGuardado.setNombrePaciente("Pedro");

        when(pacienteRepository.save(any(Paciente.class))).thenReturn(pacienteGuardado);

        // WHEN
        Paciente resultado = pacienteService.createPaciente(dto);

        // THEN
        assertNotNull(resultado);
        assertEquals("12345678", resultado.getRut());
        assertEquals("Pedro", resultado.getNombrePaciente());

        // Verificamos que se llamó a save una vez
        verify(pacienteRepository).save(any(Paciente.class));
    }

    // --- 3. Test getPacienteById (Exitoso) ---
    @Test
    void getPacienteById_DeberiaRetornarPaciente_CuandoExiste() {
        // GIVEN
        UUID id = UUID.randomUUID();
        Paciente paciente = new Paciente();
        paciente.setId(id);

        when(pacienteRepository.findById(id)).thenReturn(Optional.of(paciente));

        // WHEN
        Paciente resultado = pacienteService.getPacienteById(id);

        // THEN
        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
    }

    // --- 4. Test getPacienteById (Fallo) ---
    @Test
    void getPacienteById_DeberiaLanzarExcepcion_CuandoNoExiste() {
        // GIVEN
        UUID id = UUID.randomUUID();
        when(pacienteRepository.findById(id)).thenReturn(Optional.empty());

        // WHEN & THEN
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            pacienteService.getPacienteById(id);
        });

        assertEquals("Paciente no encontrado con ID: " + id, ex.getMessage());
    }

    // --- 5. Test updatePaciente (Exitoso) ---
    @Test
    void updatePaciente_DeberiaActualizarTodosLosCampos() {
        // GIVEN
        UUID id = UUID.randomUUID();

        // Paciente existente en BD
        Paciente existente = new Paciente();
        existente.setId(id);
        existente.setNombrePaciente("Antiguo");
        existente.setRut("11111111");

        // Datos nuevos para actualizar
        Paciente nuevosDatos = new Paciente();
        nuevosDatos.setRut("22222222");
        nuevosDatos.setDv("9");
        nuevosDatos.setNombrePaciente("Nuevo");
        nuevosDatos.setApellidoPaciente("Apellido");
        nuevosDatos.setEdad(40);
        nuevosDatos.setTelefono("55555555");

        when(pacienteRepository.findById(id)).thenReturn(Optional.of(existente));
        when(pacienteRepository.save(existente)).thenReturn(existente);

        // WHEN
        Paciente resultado = pacienteService.updatePaciente(id, nuevosDatos);

        // THEN
        // Verificamos que el objeto existente se actualizó con los nuevos valores
        assertEquals("Nuevo", resultado.getNombrePaciente());
        assertEquals("22222222", resultado.getRut());
        assertEquals("Apellido", resultado.getApellidoPaciente());
        assertEquals(40, resultado.getEdad());
        verify(pacienteRepository).save(existente);
    }

    // --- 6. Test updatePaciente (Fallo) ---
    @Test
    void updatePaciente_DeberiaLanzarExcepcion_CuandoNoExiste() {
        // GIVEN
        UUID id = UUID.randomUUID();
        Paciente nuevosDatos = new Paciente();

        when(pacienteRepository.findById(id)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(RuntimeException.class, () -> {
            pacienteService.updatePaciente(id, nuevosDatos);
        });

        verify(pacienteRepository, never()).save(any());
    }

    // --- 7. Test deletePaciente (Exitoso) ---
    @Test
    void deletePaciente_DeberiaEliminar_CuandoExiste() {
        // GIVEN
        UUID id = UUID.randomUUID();
        Paciente paciente = new Paciente();

        // Primero debe encontrarlo
        when(pacienteRepository.findById(id)).thenReturn(Optional.of(paciente));

        // WHEN
        pacienteService.deletePaciente(id);

        // THEN
        verify(pacienteRepository, times(1)).delete(paciente);
    }

    // --- 8. Test deletePaciente (Fallo) ---
    @Test
    void deletePaciente_DeberiaLanzarExcepcion_CuandoNoExiste() {
        // GIVEN
        UUID id = UUID.randomUUID();

        // Simulamos que no lo encuentra
        when(pacienteRepository.findById(id)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(RuntimeException.class, () -> {
            pacienteService.deletePaciente(id);
        });

        // Aseguramos que NUNCA llame a delete si no lo encontró
        verify(pacienteRepository, never()).delete(any());
    }
}