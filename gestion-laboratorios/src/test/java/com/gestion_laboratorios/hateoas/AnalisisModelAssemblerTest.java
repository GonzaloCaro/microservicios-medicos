package com.gestion_laboratorios.hateoas;

import com.gestion_laboratorios.model.Analisis;
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

class AnalisisModelAssemblerTest {

    private final AnalisisModelAssembler assembler = new AnalisisModelAssembler();

    @BeforeEach
    void setUp() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    void toModel_DeberiaCrearModeloConLinksCorrectos() {
        // GIVEN
        UUID id = UUID.randomUUID();
        Analisis analisis = new Analisis();
        analisis.setId(id);
        analisis.setCodigo("A001");
        analisis.setNombre("Hemograma Completo");

        // WHEN
        EntityModel<Analisis> model = assembler.toModel(analisis);

        // THEN
        assertNotNull(model);
        assertEquals(analisis, model.getContent());
        assertEquals("A001", model.getContent().getCodigo());

        assertTrue(model.getLink(IanaLinkRelations.SELF).isPresent(), "Debe existir link self");

        Link selfLink = model.getRequiredLink(IanaLinkRelations.SELF);
        String selfHref = selfLink.getHref();

        if (selfLink.isTemplated()) {
            selfHref = selfLink.expand(id).getHref();
        }

        assertTrue(selfHref.contains(id.toString()),
                "El link self (" + selfHref + ") debería contener el ID del análisis");

        assertTrue(model.getLink("analisis").isPresent(), "Debe existir link a la colección");
        String collectionHref = model.getRequiredLink("analisis").getHref();
        assertFalse(collectionHref.contains(id.toString()), "El link a la colección no debería llevar ID");
    }

    @Test
    void toModel_ConObjetoNulo_DeberiaLanzarExcepcion() {
        assertThrows(NullPointerException.class, () -> {
            assembler.toModel(null);
        });
    }
}