package com.gestion_laboratorios.hateoas;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.gestion_laboratorios.controller.PacienteController;
import com.gestion_laboratorios.model.Paciente;

@Component
public class PacienteModelAssembler implements RepresentationModelAssembler<Paciente, EntityModel<Paciente>> {

    @Override
    public EntityModel<Paciente> toModel(Paciente paciente) {
        return EntityModel.of(paciente,
                linkTo(methodOn(PacienteController.class).getPacienteById(paciente.getId())).withSelfRel(),
                linkTo(methodOn(PacienteController.class).getAllPacientes()).withRel("pacientes"));
    }
}
