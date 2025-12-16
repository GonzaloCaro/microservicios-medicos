package com.gestion_laboratorios.mapper;

import com.gestion_laboratorios.DTO.PacienteDTO;
import com.gestion_laboratorios.model.Paciente;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PacienteMapperTest {

    // Instancia directa, sin Spring Context
    private final PacienteMapper mapper = new PacienteMapper();

    @Test
    void toEntity_DeberiaMapearTodosLosCampos() {
        // GIVEN
        UUID id = UUID.randomUUID();
        PacienteDTO dto = new PacienteDTO();
        dto.setId(id);
        dto.setRut("12345678");
        dto.setDv("K");
        dto.setEdad(30);
        dto.setNombrePaciente("Juan");
        dto.setApellidoPaciente("Pérez");
        dto.setTelefono("+56912345678");

        // WHEN
        Paciente entity = mapper.toEntity(dto);

        // THEN
        assertNotNull(entity, "La entidad resultante no debería ser nula");
        assertEquals(id, entity.getId());
        assertEquals("12345678", entity.getRut());
        assertEquals("K", entity.getDv());
        assertEquals(30, entity.getEdad());
        assertEquals("Juan", entity.getNombrePaciente());
        assertEquals("Pérez", entity.getApellidoPaciente());
        assertEquals("+56912345678", entity.getTelefono());
    }

    @Test
    void toEntity_ConCamposNulos_DeberiaMapearNulos() {
        // GIVEN
        PacienteDTO dto = new PacienteDTO();
        dto.setRut("99999999");
        // Dejamos el resto en null (Nombre, Apellido, Telefono...)

        // WHEN
        Paciente entity = mapper.toEntity(dto);

        // THEN
        assertNotNull(entity);
        assertEquals("99999999", entity.getRut());
        assertNull(entity.getNombrePaciente());
        assertNull(entity.getApellidoPaciente());
        assertNull(entity.getTelefono());
    }

    @Test
    void toEntity_ConDtoNulo_DeberiaLanzarNPE() {
        // GIVEN
        PacienteDTO dtoNulo = null;

        // WHEN & THEN
        // Tu código hace dto.getId(), por lo que lanzará NullPointerException si recibe
        // null.
        assertThrows(NullPointerException.class, () -> {
            mapper.toEntity(dtoNulo);
        });
    }
}