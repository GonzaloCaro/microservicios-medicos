package com.gestion_laboratorios.laboratorios.mapper;

import org.springframework.stereotype.Component;

import com.gestion_laboratorios.laboratorios.DTO.PacienteDTO;
import com.gestion_laboratorios.laboratorios.model.Paciente;

@Component
public class PacienteMapper {
    public Paciente toEntity(PacienteDTO pacienteDTO) {
        Paciente paciente = new Paciente();
        paciente.setId(pacienteDTO.getId());
        paciente.setRut(pacienteDTO.getRut());
        paciente.setDv(pacienteDTO.getDv());
        paciente.setEdad(pacienteDTO.getEdad());
        paciente.setNombrePaciente(pacienteDTO.getNombrePaciente());
        paciente.setApellidoPaciente(pacienteDTO.getApellidoPaciente());
        paciente.setTelefono(pacienteDTO.getTelefono());
        return paciente;
    }
}
