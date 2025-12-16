package com.gestion_laboratorios.mapper;

import com.gestion_laboratorios.DTO.AnalisisDTO;
import com.gestion_laboratorios.model.Analisis;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AnalisisMapperTest {

    // Instanciamos la clase directamente (Test Unitario Puro)
    private final AnalisisMapper mapper = new AnalisisMapper();

    @Test
    void toEntity_DeberiaMapearCorrectamenteTodosLosCampos() {
        // GIVEN
        UUID id = UUID.randomUUID();
        AnalisisDTO dto = new AnalisisDTO();
        dto.setId(id);
        dto.setCodigo("A001");
        dto.setNombre("Hemograma Completo");

        // WHEN
        Analisis entity = mapper.toEntity(dto);

        // THEN
        assertNotNull(entity);
        assertEquals(id, entity.getId());
        assertEquals("A001", entity.getCodigo());
        assertEquals("Hemograma Completo", entity.getNombre());
    }

    @Test
    void toEntity_ConCamposNulos_DeberiaMapearNulos() {
        // GIVEN
        AnalisisDTO dto = new AnalisisDTO();
        dto.setId(null);
        dto.setCodigo(null);
        dto.setNombre("Solo Nombre");

        // WHEN
        Analisis entity = mapper.toEntity(dto);

        // THEN
        assertNotNull(entity);
        assertNull(entity.getId());
        assertNull(entity.getCodigo());
        assertEquals("Solo Nombre", entity.getNombre());
    }

    @Test
    void toEntity_ConDtoNulo_DeberiaLanzarExcepcion() {
        // GIVEN
        AnalisisDTO dtoNulo = null;

        // WHEN & THEN
        // Tu código actual hace dto.getId() inmediatamente, por lo que lanzará
        // NullPointerException si el DTO es null.
        // Este test valida ese comportamiento actual.
        assertThrows(NullPointerException.class, () -> {
            mapper.toEntity(dtoNulo);
        });
    }
}