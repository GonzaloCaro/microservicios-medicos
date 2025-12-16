package com.gestion_laboratorios.hateoas;

import com.gestion_laboratorios.model.Laboratorio;
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

class LaboratorioModelAssemblerTest {

    private final LaboratorioModelAssembler assembler = new LaboratorioModelAssembler();

    @BeforeEach
    void setUp() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    void toModel_DeberiaCrearModeloConSelfLink() {
        // GIVEN
        UUID id = UUID.randomUUID();
        Laboratorio laboratorio = new Laboratorio();
        laboratorio.setId(id);
        laboratorio.setNombre("Laboratorio Central");

        // WHEN
        EntityModel<Laboratorio> model = assembler.toModel(laboratorio);

        // THEN
        assertNotNull(model);
        assertTrue(model.getLink(IanaLinkRelations.SELF).isPresent());

        // Obtenemos el objeto Link
        Link selfLink = model.getRequiredLink(IanaLinkRelations.SELF);
        String href = selfLink.getHref();

        // SOLUCIÓN:
        if (selfLink.isTemplated()) {
            href = selfLink.expand(id).getHref();
        }

        System.out.println("Link final verificado: " + href);

        assertTrue(href.contains(id.toString()),
                "El link '" + href + "' debería contener el ID " + id);
    }

    @Test
    void toModel_ConLaboratorioSinId_DeberiaLanzarExcepcion() {
        Laboratorio laboratorioSinId = new Laboratorio();
        laboratorioSinId.setNombre("Lab Sin ID");

        assertThrows(IllegalArgumentException.class, () -> {
            assembler.toModel(laboratorioSinId);
        });
    }

    @Test
    void toModel_ConLaboratorioNulo_DeberiaLanzarExcepcion() {
        assertThrows(Exception.class, () -> {
            assembler.toModel(null);
        });
    }
}