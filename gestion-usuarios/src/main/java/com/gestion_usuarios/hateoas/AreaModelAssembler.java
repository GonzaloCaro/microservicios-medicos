package com.gestion_usuarios.hateoas;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import com.gestion_usuarios.controller.AreaController;
import com.gestion_usuarios.model.Area;

@Component
public class AreaModelAssembler extends RepresentationModelAssemblerSupport<Area, EntityModel<Area>> {
    @SuppressWarnings("unchecked")
    public AreaModelAssembler() {
        super(AreaController.class, (Class<EntityModel<Area>>) (Class<?>) EntityModel.class);
    }

    @Override
    public EntityModel<Area> toModel(Area area) {
        EntityModel<Area> model = createModelWithId(area.getId(), area);
        return EntityModel.of(area,
                linkTo(methodOn(AreaController.class).getAreaById(area.getId())).withSelfRel());
    }

}
