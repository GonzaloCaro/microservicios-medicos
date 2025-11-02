package com.gestion_laboratorios.laboratorios.hateoas;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.gestion_laboratorios.laboratorios.controller.AnalisisController;
import com.gestion_laboratorios.laboratorios.model.Analisis;

@Component
public class AnalisisModelAssembler implements RepresentationModelAssembler<Analisis, EntityModel<Analisis>> {

    @Override
    public EntityModel<Analisis> toModel(Analisis analisis) {
        return EntityModel.of(analisis,
                linkTo(methodOn(AnalisisController.class).getAnalisisById(analisis.getId())).withSelfRel(),
                linkTo(methodOn(AnalisisController.class).getAllAnalisis()).withRel("analisis"));
    }

}
