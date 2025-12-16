package com.gestion_laboratorios.hateoas;

import com.gestion_laboratorios.model.Asignacion;
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

class AsignacionModelAssemblerTest {

    private final AsignacionModelAssembler assembler = new AsignacionModelAssembler();

    @BeforeEach
    void setUp() {
        // Configuramos el contexto HTTP simulado (necesario para linkTo y
        // createModelWithId)
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    void toModel_DeberiaCrearModeloConSelfLink() {
        // GIVEN
        UUID id = UUID.randomUUID();
        Asignacion asignacion = new Asignacion();
        asignacion.setId(id);
        asignacion.setDetalle("Asignación de prueba");

        // WHEN
        EntityModel<Asignacion> model = assembler.toModel(asignacion);

        // THEN
        // 1. Validar contenido
        assertNotNull(model);
        assertEquals(asignacion, model.getContent());
        assertEquals("Asignación de prueba", model.getContent().getDetalle());

        // 2. Validar Link SELF
        assertTrue(model.getLink(IanaLinkRelations.SELF).isPresent(), "Debe existir link self");

        Link selfLink = model.getRequiredLink(IanaLinkRelations.SELF);
        String href = selfLink.getHref();

        // Expansión manual del Template si es necesario (para evitar fallos si falta
        // @PathVariable en controller)
        if (selfLink.isTemplated()) {
            href = selfLink.expand(id).getHref();
        }

        assertTrue(href.contains(id.toString()),
                "El link '" + href + "' debería contener el ID de la asignación");
    }

    @Test
    void toModel_ConAsignacionSinId_DeberiaLanzarExcepcion() {
        // GIVEN
        Asignacion asignacionSinId = new Asignacion();
        // ID es null

        // WHEN & THEN
        // 'createModelWithId' de Spring HATEOAS lanza IllegalArgumentException si el ID
        // es nulo
        assertThrows(IllegalArgumentException.class, () -> {
            assembler.toModel(asignacionSinId);
        });
    }

    @Test
    void toModel_ConObjetoNulo_DeberiaLanzarNPE() {
        // GIVEN
        // Pasamos null

        // WHEN & THEN
        // Tu código hace: createModelWithId(asignacion.getId(), ...)
        // Al intentar acceder a .getId() sobre un null, Java lanza
        // NullPointerException.
        assertThrows(NullPointerException.class, () -> {
            assembler.toModel(null);
        });
    }
}