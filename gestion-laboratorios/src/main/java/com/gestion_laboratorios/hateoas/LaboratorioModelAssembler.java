package com.gestion_laboratorios.hateoas;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import com.gestion_laboratorios.controller.LaboratorioController;
import com.gestion_laboratorios.model.Laboratorio;

@Component
public class LaboratorioModelAssembler
        extends RepresentationModelAssemblerSupport<Laboratorio, EntityModel<Laboratorio>> {
    @SuppressWarnings("unchecked")
    public LaboratorioModelAssembler() {
        super(LaboratorioController.class, (Class<EntityModel<Laboratorio>>) (Class<?>) EntityModel.class);
    }

    @Override
    public EntityModel<Laboratorio> toModel(Laboratorio laboratorio) {
        EntityModel<Laboratorio> laboratorioModel = createModelWithId(laboratorio.getId(), laboratorio);
        return EntityModel.of(laboratorio,
                linkTo(methodOn(LaboratorioController.class).getLaboratorioById(laboratorio.getId())).withSelfRel());

    }

}
