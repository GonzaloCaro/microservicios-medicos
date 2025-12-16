package com.gestion_usuarios.mapper;

import com.gestion_usuarios.DTO.UsuarioDTO;
import com.gestion_usuarios.model.Usuario;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioMapperTest {

    private final UsuarioMapper mapper = new UsuarioMapper();

    // --- TEST: toEntity (DTO -> Entity) ---

    @Test
    void toEntity_ConDatosBasicos_DeberiaMapearCamposSimples() {
        // GIVEN: DTO sin rol ni área
        UUID id = UUID.randomUUID();
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(id);
        dto.setNombre("Carlos");
        dto.setApellido("Diaz");
        dto.setUserName("cdiaz");
        dto.setEmail("carlos@mail.com");
        dto.setContrasena("pass123");
        // roleId y areaId son null por defecto

        // WHEN
        Usuario entity = mapper.toEntity(dto);

        // THEN
        assertNotNull(entity);
        assertEquals(id, entity.getId());
        assertEquals("Carlos", entity.getNombre());
        assertEquals("cdiaz", entity.getUserName());

        // Verificamos que NO se creó la relación RoleUser
        assertNull(entity.getRole(), "Si no hay roleId/areaId, el rol debería ser null");
    }

    @Test
    void toEntity_ConRolYArea_DeberiaCrearEstructuraRoleUser() {
        // GIVEN: DTO con IDs de rol y área
        UUID roleId = UUID.randomUUID();
        UUID areaId = UUID.randomUUID();

        UsuarioDTO dto = new UsuarioDTO();
        dto.setUserName("admin");
        dto.setRoleId(roleId);
        dto.setAreaId(areaId);

        // WHEN
        Usuario entity = mapper.toEntity(dto);

        // THEN
        assertNotNull(entity);

        // 1. Verificamos que se creó el objeto intermedio RoleUser
        assertNotNull(entity.getRole(), "Se debería haber creado el objeto RoleUser");

        // 2. Verificamos que dentro de RoleUser se setearon los IDs correctos
        assertEquals(roleId, entity.getRole().getRol().getId());
        assertEquals(areaId, entity.getRole().getArea().getId());

        // 3. Verificamos la relación bidireccional (roleUser.setUsuario(usuario))
        assertEquals(entity, entity.getRole().getUsuario(), "El RoleUser debe apuntar de vuelta al Usuario");
    }

    @Test
    void toEntity_SiFaltaUnId_NoDeberiaCrearRoleUser() {
        // GIVEN: Tu código usa && (AND), así que si falta uno, no entra al if.
        UsuarioDTO dto = new UsuarioDTO();
        dto.setRoleId(UUID.randomUUID());
        dto.setAreaId(null); // Falta el área

        // WHEN
        Usuario entity = mapper.toEntity(dto);

        // THEN
        assertNull(entity.getRole(), "No debería crear RoleUser si falta el AreaId");
    }

    @Test
    void toEntity_ConDtoNulo_DeberiaLanzarNPE() {
        assertThrows(NullPointerException.class, () -> mapper.toEntity(null));
    }

    // --- TEST: toDTO (Entity -> DTO) ---

    @Test
    void toDTO_DeberiaMapearCamposPlanos() {
        // GIVEN
        UUID id = UUID.randomUUID();
        Usuario entity = new Usuario();
        entity.setId(id);
        entity.setNombre("Ana");
        entity.setApellido("Perez");
        entity.setUserName("aperez");
        entity.setEmail("ana@mail.com");
        entity.setContrasena("secret");

        // Nota: Tu método toDTO actual NO mapea roleId ni areaId de vuelta,
        // solo mapea los campos planos del usuario. Probamos eso.

        // WHEN
        UsuarioDTO dto = mapper.toDTO(entity);

        // THEN
        assertNotNull(dto);
        assertEquals(id, dto.getId());
        assertEquals("Ana", dto.getNombre());
        assertEquals("aperez", dto.getUserName());
        assertEquals("secret", dto.getContrasena());
    }

    @Test
    void toDTO_ConEntidadNula_DeberiaLanzarNPE() {
        assertThrows(NullPointerException.class, () -> mapper.toDTO(null));
    }
}