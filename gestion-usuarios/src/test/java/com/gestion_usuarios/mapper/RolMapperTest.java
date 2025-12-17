package com.gestion_usuarios.mapper;

import com.gestion_usuarios.DTO.RolDTO;
import com.gestion_usuarios.model.Rol;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RolMapperTest {

    private final RolMapper mapper = new RolMapper();

    // --- TEST: toDTO (Entity -> DTO) ---
    @Test
    void toDTO_DeberiaMapearTodosLosCampos() {
        // GIVEN
        UUID id = UUID.randomUUID();
        Rol rol = new Rol();
        rol.setId(id);
        rol.setNombre("ROLE_ADMIN");

        // WHEN
        RolDTO dto = mapper.toDTO(rol);

        // THEN
        assertNotNull(dto);
        assertEquals(id, dto.getId());
        assertEquals("ROLE_ADMIN", dto.getNombre());
    }

    @Test
    void toDTO_ConEntidadNula_DeberiaLanzarNPE() {
        // GIVEN
        Rol rolNulo = null;

        // WHEN & THEN
        // Tu código ejecuta rol.getId() sin verificar null, esperando
        // NullPointerException
        assertThrows(NullPointerException.class, () -> {
            mapper.toDTO(rolNulo);
        });
    }

    // --- TEST: toEntity (DTO -> Entity) ---
    @Test
    void toEntity_DeberiaMapearTodosLosCampos() {
        // GIVEN
        UUID id = UUID.randomUUID();
        RolDTO dto = new RolDTO();
        dto.setId(id);
        dto.setNombre("ROLE_USER");

        // WHEN
        Rol entity = mapper.toEntity(dto);

        // THEN
        assertNotNull(entity);
        assertEquals(id, entity.getId());
        assertEquals("ROLE_USER", entity.getNombre());
    }

    @Test
    void toEntity_ConDtoNulo_DeberiaLanzarNPE() {
        // GIVEN
        RolDTO dtoNulo = null;

        // WHEN & THEN
        // Tu código ejecuta rolDTO.getId() sin verificar null
        assertThrows(NullPointerException.class, () -> {
            mapper.toEntity(dtoNulo);
        });
    }
}