package com.gestion_laboratorios.mapper.response;

import com.gestion_laboratorios.DTO.response.AsignacionResponseDTO;
import com.gestion_laboratorios.model.Analisis;
import com.gestion_laboratorios.model.Asignacion;
import com.gestion_laboratorios.model.Laboratorio;
import com.gestion_laboratorios.model.Paciente;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AsignacionResponseMapperTest {

    // Instancia directa (Test Unitario Puro)
    private final AsignacionResponseMapper mapper = new AsignacionResponseMapper();

    // --- TEST 1: Mapeo Completo (Happy Path) ---
    @Test
    void toDto_DeberiaMapearTodoElArbolDeObjetos() {
        // GIVEN: Preparamos un objeto Asignacion con TODAS sus relaciones llenas

        // 1. Relación Analisis
        Analisis analisis = new Analisis();
        analisis.setId(UUID.randomUUID());
        analisis.setCodigo("A100");
        analisis.setNombre("Perfil Lipídico");

        // 2. Relación Paciente
        Paciente paciente = new Paciente();
        paciente.setId(UUID.randomUUID());
        paciente.setNombrePaciente("Maria");
        paciente.setApellidoPaciente("Gomez");
        paciente.setRut("12345678");
        paciente.setDv("K");
        paciente.setEdad(25);
        paciente.setTelefono("987654321");

        // 3. Relación Laboratorio
        Laboratorio laboratorio = new Laboratorio();
        laboratorio.setId(UUID.randomUUID());
        laboratorio.setNombre("Lab Central");
        laboratorio.setUbicacion("Piso 1");

        // 4. Objeto Principal (Asignacion)
        Asignacion asignacion = new Asignacion();
        asignacion.setId(UUID.randomUUID());
        asignacion.setUsuarioId(UUID.randomUUID());
        asignacion.setFechaAsignacion(LocalDateTime.now());
        asignacion.setDetalle("Chequeo Anual");

        // Asignamos las relaciones
        asignacion.setAnalisis(analisis);
        asignacion.setPaciente(paciente);
        asignacion.setLaboratorio(laboratorio);

        // WHEN
        AsignacionResponseDTO result = mapper.toDto(asignacion);

        // THEN
        assertNotNull(result);
        assertEquals(asignacion.getId(), result.getId());
        assertEquals(asignacion.getUsuarioId(), result.getUsuarioId());
        assertEquals("Chequeo Anual", result.getDetalle());
        assertEquals(asignacion.getFechaAsignacion(), result.getFechaAsignacion());

        // Verificamos que los DTOs anidados no sean nulos y tengan datos
        assertNotNull(result.getAnalisis());
        assertEquals("A100", result.getAnalisis().getCodigo());

        assertNotNull(result.getPaciente());
        assertEquals("Maria", result.getPaciente().getNombrePaciente());
        assertEquals("K", result.getPaciente().getDv());
        assertEquals(25, result.getPaciente().getEdad());

        assertNotNull(result.getLaboratorio());
        assertEquals("Lab Central", result.getLaboratorio().getNombre());
    }

    // --- TEST 2: Objeto Nulo ---
    @Test
    void toDto_ConInputNulo_DeberiaRetornarNull() {
        // Tu código tiene un "if (asignacion == null) return null;" al principio
        assertNull(mapper.toDto(null));
    }

    // --- TEST 3: Relaciones Nulas (Partial Mapping) ---
    @Test
    void toDto_ConRelacionesNulas_DeberiaMapearCamposPlanosYDejarRelacionesNull() {
        // GIVEN: Una asignación que tiene datos propios, pero sus relaciones son null
        Asignacion asignacion = new Asignacion();
        asignacion.setId(UUID.randomUUID());
        asignacion.setDetalle("Solo datos básicos");
        // No seteamos analisis, paciente ni laboratorio (son null)

        // WHEN
        AsignacionResponseDTO result = mapper.toDto(asignacion);

        // THEN
        assertNotNull(result);
        assertEquals("Solo datos básicos", result.getDetalle());

        // Verificamos que los métodos privados manejaron el null correctamente
        // y no lanzaron NullPointerException
        assertNull(result.getAnalisis());
        assertNull(result.getPaciente());
        assertNull(result.getLaboratorio());
    }
}