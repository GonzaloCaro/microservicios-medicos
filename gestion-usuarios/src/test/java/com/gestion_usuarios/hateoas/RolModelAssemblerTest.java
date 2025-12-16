package com.gestion_usuarios.hateoas;

import com.gestion_usuarios.model.Rol;
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

class RolModelAssemblerTest {

    private final RolModelAssembler assembler = new RolModelAssembler();

    @BeforeEach
    void setUp() {
        // --- CONFIGURACIÓN DE CONTEXTO HTTP ---
        // Necesario para que linkTo() y methodOn() funcionen sin levantar el servidor
        // real.
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    void toModel_DeberiaCrearModeloConSelfLink() {
        // GIVEN
        UUID id = UUID.randomUUID();
        Rol rol = new Rol();
        rol.setId(id);
        rol.setNombre("ROLE_ADMIN");

        // WHEN
        EntityModel<Rol> model = assembler.toModel(rol);

        // THEN
        // 1. Validar integridad del contenido
        assertNotNull(model);
        assertEquals(rol, model.getContent());
        assertEquals("ROLE_ADMIN", model.getContent().getNombre());

        // 2. Validar presencia del Link SELF
        assertTrue(model.getLink(IanaLinkRelations.SELF).isPresent(), "Debe existir el link 'self'");

        // 3. Validar URL del Link
        Link selfLink = model.getRequiredLink(IanaLinkRelations.SELF);
        String href = selfLink.getHref();

        // Manejo de Templates: Si el link se genera como /api/roles/{id}, lo
        // expandimos.
        if (selfLink.isTemplated()) {
            href = selfLink.expand(id).getHref();
        }

        assertTrue(href.contains(id.toString()),
                "El link '" + href + "' debería contener el ID del rol (" + id + ")");
    }

    @Test
    void toModel_ConRolSinId_DeberiaLanzarExcepcion() {
        // GIVEN
        Rol rolSinId = new Rol();
        rolSinId.setNombre("ROLE_NEW");
        // ID es null

        // WHEN & THEN
        // createModelWithId (llamado internamente) valida que el ID no sea nulo.
        assertThrows(IllegalArgumentException.class, () -> {
            assembler.toModel(rolSinId);
        });
    }

    @Test
    void toModel_ConRolNulo_DeberiaLanzarNPE() {
        // GIVEN
        Rol rolNulo = null;

        // WHEN & THEN
        // Tu código llama a rol.getId() inmediatamente, causando NPE.
        assertThrows(NullPointerException.class, () -> {
            assembler.toModel(rolNulo);
        });
    }
}