package com.gestion_usuarios.hateoas;

import com.gestion_usuarios.model.Usuario;
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

class UsuarioModelAssemblerTest {

    private final UsuarioModelAssembler assembler = new UsuarioModelAssembler();

    @BeforeEach
    void setUp() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    void toModel_DeberiaCrearModeloConSelfLink() {
        // GIVEN
        UUID id = UUID.randomUUID();
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setUserName("admin");
        usuario.setEmail("admin@test.com");

        // WHEN
        EntityModel<Usuario> model = assembler.toModel(usuario);

        // THEN
        // 1. Validar contenido
        assertNotNull(model);
        assertEquals(usuario, model.getContent());
        assertEquals("admin", model.getContent().getUserName());

        // 2. Validar existencia del Link SELF
        assertTrue(model.getLink(IanaLinkRelations.SELF).isPresent(), "Debe existir el link self");

        // 3. Validar contenido del Link
        Link selfLink = model.getRequiredLink(IanaLinkRelations.SELF);
        String href = selfLink.getHref();

        // LÓGICA ROBUSTA:
        // Si el Controller no tiene @PathVariable explícito, HATEOAS genera un
        // template: .../{id}
        // Si es así, lo expandimos manualmente para verificar que el ID sea correcto.
        if (selfLink.isTemplated()) {
            href = selfLink.expand(id).getHref();
        }

        // Verificamos que el link final contenga el ID del usuario
        assertTrue(href.contains(id.toString()),
                "El link '" + href + "' debería contener el ID " + id);
    }

    @Test
    void toModel_ConUsuarioSinId_DeberiaLanzarExcepcion() {
        // GIVEN
        Usuario usuarioSinId = new Usuario();
        usuarioSinId.setUserName("no_id");
        // ID es null

        // WHEN & THEN
        // 'createModelWithId' (método padre de Spring HATEOAS) valida que el ID no sea
        // nulo
        // y lanza IllegalArgumentException si lo es.
        assertThrows(IllegalArgumentException.class, () -> {
            assembler.toModel(usuarioSinId);
        });
    }

    @Test
    void toModel_ConUsuarioNulo_DeberiaLanzarNPE() {
        // GIVEN
        Usuario usuarioNulo = null;

        // WHEN & THEN
        // Tu código ejecuta 'usuario.getId()' en la primera línea.
        // Al ser null, Java lanza NullPointerException inmediatamente.
        assertThrows(NullPointerException.class, () -> {
            assembler.toModel(usuarioNulo);
        });
    }
}