package com.gestion_laboratorios.mapper;

import com.gestion_laboratorios.DTO.LaboratorioDTO;
import com.gestion_laboratorios.model.Laboratorio;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class LaboratorioMapperTest {

    // Instancia directa (Test Unitario Puro)
    private final LaboratorioMapper mapper = new LaboratorioMapper();

    // --- TEST: toEntity (DTO -> Entity) ---
    @Test
    void toEntity_DeberiaMapearTodosLosCampos() {
        // GIVEN
        UUID id = UUID.randomUUID();
        LaboratorioDTO dto = new LaboratorioDTO();
        dto.setId(id);
        dto.setNombre("Laboratorio Central");
        dto.setUbicacion("Edificio B, Piso 2");

        // WHEN
        Laboratorio entity = mapper.toEntity(dto);

        // THEN
        assertNotNull(entity);
        assertEquals(id, entity.getId());
        assertEquals("Laboratorio Central", entity.getNombre());
        assertEquals("Edificio B, Piso 2", entity.getUbicacion());
    }

    @Test
    void toEntity_ConDtoNulo_DeberiaLanzarNPE() {
        // Validación del comportamiento actual:
        // Tu código hace dto.getId() sin verificar null, así que lanza NPE.
        assertThrows(NullPointerException.class, () -> {
            mapper.toEntity(null);
        });
    }

    // --- TEST: toDTO (Entity -> DTO) ---
    @Test
    void toDTO_DeberiaMapearTodosLosCampos() {
        // GIVEN
        UUID id = UUID.randomUUID();
        Laboratorio entity = new Laboratorio();
        entity.setId(id);
        entity.setNombre("Laboratorio Sur");
        entity.setUbicacion("Edificio C");

        // WHEN
        LaboratorioDTO dto = mapper.toDTO(entity);

        // THEN
        assertNotNull(dto);
        assertEquals(id, dto.getId());
        assertEquals("Laboratorio Sur", dto.getNombre());
        assertEquals("Edificio C", dto.getUbicacion());
    }

    @Test
    void toDTO_ConEntidadNula_DeberiaLanzarNPE() {
        // Validación del comportamiento actual:
        // Tu código hace laboratorio.getId() sin verificar null, así que lanza NPE.
        assertThrows(NullPointerException.class, () -> {
            mapper.toDTO(null);
        });
    }
}