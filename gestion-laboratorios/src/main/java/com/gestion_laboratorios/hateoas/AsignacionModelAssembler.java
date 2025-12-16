package com.gestion_laboratorios.hateoas;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import com.gestion_laboratorios.controller.AsignacionController;
import com.gestion_laboratorios.model.Asignacion;

@Component
public class AsignacionModelAssembler extends RepresentationModelAssemblerSupport<Asignacion, EntityModel<Asignacion>> {
    @SuppressWarnings("unchecked")
    public AsignacionModelAssembler() {
        super(AsignacionController.class, (Class<EntityModel<Asignacion>>) (Class<?>) EntityModel.class);
    }

    @Override
    public EntityModel<Asignacion> toModel(Asignacion asignacion) {
        EntityModel<Asignacion> asignacionModel = createModelWithId(asignacion.getId(), asignacion);
        return EntityModel.of(asignacion,
                linkTo(methodOn(AsignacionController.class).getAsignacionById(asignacion.getId())).withSelfRel());

    }

}
