package com.gestion_laboratorios.mapper;

import com.gestion_laboratorios.DTO.AsignacionDTO;
import com.gestion_laboratorios.DTO.response.AsignacionResponseDTO;
import com.gestion_laboratorios.model.Analisis;
import com.gestion_laboratorios.model.Asignacion;
import com.gestion_laboratorios.model.Laboratorio;
import com.gestion_laboratorios.model.Paciente;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AsignacionMapperTest {

    // Instancia directa, sin mocks.
    private final AsignacionMapper mapper = new AsignacionMapper();

    // --- TEST 1: toEntity (DTO -> Entity) ---
    @Test
    void toEntity_DeberiaMapearCamposPlanos() {
        // GIVEN
        UUID id = UUID.randomUUID();
        UUID labId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();
        LocalDateTime fecha = LocalDateTime.now();

        AsignacionDTO dto = new AsignacionDTO();
        dto.setId(id);
        dto.setLaboratorioId(labId);
        dto.setUsuarioId(usuarioId);
        dto.setDetalle("Detalle prueba");
        dto.setFechaAsignacion(fecha);

        // WHEN
        Asignacion entity = mapper.toEntity(dto);

        // THEN
        assertNotNull(entity);
        assertEquals(id, entity.getId());
        assertEquals(labId, entity.getLaboratorioId());
        assertEquals(usuarioId, entity.getUsuarioId());
        assertEquals("Detalle prueba", entity.getDetalle());
        assertEquals(fecha, entity.getFechaAsignacion());
    }

    @Test
    void toEntity_ConDtoNulo_DeberiaLanzarNPE() {
        // Validación del comportamiento actual de tu código
        assertThrows(NullPointerException.class, () -> mapper.toEntity(null));
    }

    // --- TEST 2: toDto (Entity -> ResponseDTO Completo) ---
    @Test
    void toDto_DeberiaMapearTodoIncluyendoAnidados() {
        // GIVEN
        // 1. Preparamos las entidades anidadas
        Analisis analisis = new Analisis();
        analisis.setId(UUID.randomUUID());
        analisis.setCodigo("A001");
        analisis.setNombre("Hemograma");

        Paciente paciente = new Paciente();
        paciente.setId(UUID.randomUUID());
        paciente.setNombrePaciente("Juan");
        paciente.setRut("12345678");

        Laboratorio laboratorio = new Laboratorio();
        laboratorio.setId(UUID.randomUUID());
        laboratorio.setNombre("Lab Central");

        // 2. Preparamos la Asignacion principal
        Asignacion asignacion = new Asignacion();
        asignacion.setId(UUID.randomUUID());
        asignacion.setUsuarioId(UUID.randomUUID());
        asignacion.setDetalle("Test Completo");
        asignacion.setFechaAsignacion(LocalDateTime.now());

        // Asignamos las relaciones
        asignacion.setAnalisis(analisis);
        asignacion.setPaciente(paciente);
        asignacion.setLaboratorio(laboratorio);

        // WHEN
        AsignacionResponseDTO result = mapper.toDto(asignacion);

        // THEN
        assertNotNull(result);
        assertEquals("Test Completo", result.getDetalle());

        // Verificamos mapeo anidado de Analisis
        assertNotNull(result.getAnalisis());
        assertEquals("A001", result.getAnalisis().getCodigo());
        assertEquals("Hemograma", result.getAnalisis().getNombre());

        // Verificamos mapeo anidado de Paciente
        assertNotNull(result.getPaciente());
        assertEquals("Juan", result.getPaciente().getNombrePaciente());
        assertEquals("12345678", result.getPaciente().getRut());

        // Verificamos mapeo anidado de Laboratorio
        assertNotNull(result.getLaboratorio());
        assertEquals("Lab Central", result.getLaboratorio().getNombre());
    }

    // --- TEST 3: toDto (Manejo de Nulos) ---
    @Test
    void toDto_ConEntidadNula_DeberiaRetornarNull() {
        assertNull(mapper.toDto(null));
    }

    @Test
    void toDto_ConRelacionesNulas_DeberiaMapearCamposEnNull() {
        // GIVEN
        Asignacion asignacion = new Asignacion();
        asignacion.setId(UUID.randomUUID());
        asignacion.setDetalle("Sin relaciones");
        // Dejamos analisis, paciente y laboratorio en null

        // WHEN
        AsignacionResponseDTO result = mapper.toDto(asignacion);

        // THEN
        assertNotNull(result);
        assertEquals("Sin relaciones", result.getDetalle());

        // Los objetos anidados deben ser null (gracias a los if en tus métodos
        // privados)
        assertNull(result.getAnalisis());
        assertNull(result.getPaciente());
        assertNull(result.getLaboratorio());
    }
}