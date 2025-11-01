package com.gestion_usuarios.usuarios.hateoas;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import com.gestion_usuarios.usuarios.controller.RolController;
import com.gestion_usuarios.usuarios.model.Rol;

@Component
public class RolModelAssembler extends RepresentationModelAssemblerSupport<Rol, EntityModel<Rol>> {
    @SuppressWarnings("unchecked")
    public RolModelAssembler() {
        super(RolController.class, (Class<EntityModel<Rol>>) (Class<?>) EntityModel.class);
    }

    @Override
    public EntityModel<Rol> toModel(Rol rol) {
        EntityModel<Rol> model = createModelWithId(rol.getId(), rol);
        return EntityModel.of(rol,
                linkTo(methodOn(RolController.class).getRolById(rol.getId())).withSelfRel());
    }

}
