package com.gestion_laboratorios.hateoas;

import com.gestion_laboratorios.model.Paciente;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PacienteModelAssemblerTest {

    // Instanciamos el assembler directamente (es una clase simple)
    private final PacienteModelAssembler assembler = new PacienteModelAssembler();

    @Test
    void toModel_DeberiaConvertirPacienteYAgregarLinks() {
        // GIVEN
        UUID id = UUID.randomUUID();
        Paciente paciente = new Paciente();
        paciente.setId(id);
        paciente.setNombrePaciente("Juan");
        paciente.setRut("12345678");

        // WHEN
        EntityModel<Paciente> model = assembler.toModel(paciente);

        // THEN
        // 1. Verificar que el contenido (la entidad) sea el mismo
        assertEquals(paciente, model.getContent());
        assertEquals("Juan", model.getContent().getNombrePaciente());

        // 2. Verificar Link "SELF"
        // Debe existir un link con relación "self"
        assertTrue(model.getLink(IanaLinkRelations.SELF).isPresent());
        // Verificamos que la URL contenga el ID del paciente
        String selfLinkHref = model.getRequiredLink(IanaLinkRelations.SELF).getHref();
        assertTrue(selfLinkHref.endsWith("/" + id)); // Asumiendo que tu ruta es /api/pacientes/{id}

        // 3. Verificar Link "pacientes" (Colección)
        assertTrue(model.getLink("pacientes").isPresent());
        String collectionLinkHref = model.getRequiredLink("pacientes").getHref();
        assertFalse(collectionLinkHref.contains(id.toString())); // La colección no debe llevar el ID
    }

}