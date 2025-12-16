package com.gestion_usuarios.mapper;

import com.gestion_usuarios.DTO.AreaDTO;
import com.gestion_usuarios.model.Area;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AreaMapperTest {

    // Instancia directa (Test Unitario Puro)
    private final AreaMapper mapper = new AreaMapper();

    // --- TEST: toEntity (DTO -> Entity) ---
    @Test
    void toEntity_DeberiaMapearCorrectamente() {
        // GIVEN
        UUID id = UUID.randomUUID();
        AreaDTO dto = new AreaDTO();
        dto.setId(id);
        dto.setNombre("Recursos Humanos");

        // WHEN
        Area entity = mapper.toEntity(dto);

        // THEN
        assertNotNull(entity);
        assertEquals(id, entity.getId());
        assertEquals("Recursos Humanos", entity.getNombre());
    }

    @Test
    void toEntity_ConDtoNulo_DeberiaLanzarNPE() {
        // GIVEN
        AreaDTO dtoNulo = null;

        // WHEN & THEN
        // Tu código hace dto.getId() sin validar null, por lo que lanza
        // NullPointerException.
        assertThrows(NullPointerException.class, () -> {
            mapper.toEntity(dtoNulo);
        });
    }

    // --- TEST: toDTO (Entity -> DTO) ---
    @Test
    void toDTO_DeberiaMapearCorrectamente() {
        // GIVEN
        UUID id = UUID.randomUUID();
        Area entity = new Area();
        entity.setId(id);
        entity.setNombre("Tecnología");

        // WHEN
        AreaDTO dto = mapper.toDTO(entity);

        // THEN
        assertNotNull(dto);
        assertEquals(id, dto.getId());
        assertEquals("Tecnología", dto.getNombre());
    }

    @Test
    void toDTO_ConEntidadNula_DeberiaLanzarNPE() {
        // GIVEN
        Area entityNulo = null;

        // WHEN & THEN
        // Tu código hace entity.getId() sin validar null, por lo que lanza
        // NullPointerException.
        assertThrows(NullPointerException.class, () -> {
            mapper.toDTO(entityNulo);
        });
    }
}