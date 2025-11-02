package com.gestion_laboratorios.laboratorios.mapper.response;

import org.springframework.stereotype.Component;

import com.gestion_laboratorios.laboratorios.DTO.AnalisisDTO;
import com.gestion_laboratorios.laboratorios.DTO.LaboratorioDTO;
import com.gestion_laboratorios.laboratorios.DTO.PacienteDTO;
import com.gestion_laboratorios.laboratorios.DTO.response.AsignacionResponseDTO;
import com.gestion_laboratorios.laboratorios.model.Analisis;
import com.gestion_laboratorios.laboratorios.model.Asignacion;
import com.gestion_laboratorios.laboratorios.model.Laboratorio;
import com.gestion_laboratorios.laboratorios.model.Paciente;

@Component
public class AsignacionResponseMapper {

    public AsignacionResponseDTO toDto(Asignacion asignacion) {
        if (asignacion == null)
            return null;

        return AsignacionResponseDTO.builder()
                .id(asignacion.getId())
                .usuarioId(asignacion.getUsuarioId())
                .fechaAsignacion(asignacion.getFechaAsignacion())
                .detalle(asignacion.getDetalle())
                .analisis(toAnalisisDto(asignacion.getAnalisis()))
                .paciente(toPacienteDto(asignacion.getPaciente()))
                .laboratorio(toLaboratorioDto(asignacion.getLaboratorio()))
                .build();
    }

    private AnalisisDTO toAnalisisDto(Analisis analisis) {
        if (analisis == null)
            return null;
        return AnalisisDTO.builder()
                .id(analisis.getId())
                .codigo(analisis.getCodigo())
                .nombre(analisis.getNombre())
                .build();
    }

    private PacienteDTO toPacienteDto(Paciente paciente) {
        if (paciente == null)
            return null;
        return PacienteDTO.builder()
                .id(paciente.getId())
                .rut(paciente.getRut())
                .dv(paciente.getDv())
                .nombrePaciente(paciente.getNombrePaciente())
                .apellidoPaciente(paciente.getApellidoPaciente())
                .edad(paciente.getEdad())
                .telefono(paciente.getTelefono())
                .build();
    }

    private LaboratorioDTO toLaboratorioDto(Laboratorio lab) {
        if (lab == null)
            return null;
        return LaboratorioDTO.builder()
                .id(lab.getId())
                .nombre(lab.getNombre())
                .ubicacion(lab.getUbicacion())
                .build();
    }
}
