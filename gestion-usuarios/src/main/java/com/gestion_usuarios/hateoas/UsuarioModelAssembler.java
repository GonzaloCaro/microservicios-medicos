package com.gestion_usuarios.hateoas;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import com.gestion_usuarios.controller.UsuarioController;
import com.gestion_usuarios.model.Usuario;

@Component
public class UsuarioModelAssembler extends RepresentationModelAssemblerSupport<Usuario, EntityModel<Usuario>> {
    @SuppressWarnings("unchecked")
    public UsuarioModelAssembler() {
        super(UsuarioController.class, (Class<EntityModel<Usuario>>) (Class<?>) EntityModel.class);
    }

    @Override
    public EntityModel<Usuario> toModel(Usuario usuario) {
        EntityModel<Usuario> usuarioModel = createModelWithId(usuario.getId(), usuario);
        return EntityModel.of(usuario,
                linkTo(methodOn(UsuarioController.class).getUsuarioById(usuario.getId())).withSelfRel());

    }
}
