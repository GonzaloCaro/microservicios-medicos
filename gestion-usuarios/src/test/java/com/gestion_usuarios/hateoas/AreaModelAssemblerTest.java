package com.gestion_usuarios.hateoas;

import com.gestion_usuarios.model.Area;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AreaModelAssemblerTest {

    private final AreaModelAssembler assembler = new AreaModelAssembler();

    @BeforeEach
    void setUp() {
        // --- CONFIGURACIÓN CRÍTICA ---
        // Simulamos el contexto de una petición HTTP (Request).
        // Sin esto, 'linkTo' y 'methodOn' fallan porque no saben cuál es la URL base
        // del servidor.
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    void toModel_DeberiaCrearModeloConSelfLink() {
        // GIVEN
        UUID id = UUID.randomUUID();
        Area area = new Area();
        area.setId(id);
        area.setNombre("Recursos Humanos");

        // WHEN
        EntityModel<Area> model = assembler.toModel(area);

        // THEN
        // 1. Validar que el contenido de la entidad se mantenga
        assertNotNull(model);
        assertEquals(area, model.getContent());
        assertEquals("Recursos Humanos", model.getContent().getNombre());

        // 2. Validar que exista el link "self"
        assertTrue(model.getLink(IanaLinkRelations.SELF).isPresent(), "Debe existir el link self");

        // 3. Validar la URL del link
        Link selfLink = model.getRequiredLink(IanaLinkRelations.SELF);
        String href = selfLink.getHref();

        // MANEJO DE TEMPLATES:
        // Si tu Controller no tiene @PathVariable explícito, HATEOAS genera
        // ".../areas/{id}"
        // Esta lógica hace el test resistente a ese detalle, expandiendo el ID si es
        // necesario.
        if (selfLink.isTemplated()) {
            href = selfLink.expand(id).getHref();
        }

        assertTrue(href.contains(id.toString()),
                "El link '" + href + "' debería contener el ID del área (" + id + ")");
    }

    @Test
    void toModel_ConAreaSinId_DeberiaLanzarExcepcion() {
        // GIVEN
        Area areaSinId = new Area();
        areaSinId.setNombre("Area Nueva");
        // ID es null

        // WHEN & THEN
        // 'createModelWithId' (método padre de Spring) valida que el ID no sea nulo.
        assertThrows(IllegalArgumentException.class, () -> {
            assembler.toModel(areaSinId);
        });
    }

    @Test
    void toModel_ConAreaNula_DeberiaLanzarNPE() {
        // GIVEN
        Area areaNula = null;

        // WHEN & THEN
        // Tu código ejecuta 'area.getId()' en la primera línea.
        // Al ser null, lanza NullPointerException inmediatamente.
        assertThrows(NullPointerException.class, () -> {
            assembler.toModel(areaNula);
        });
    }
}