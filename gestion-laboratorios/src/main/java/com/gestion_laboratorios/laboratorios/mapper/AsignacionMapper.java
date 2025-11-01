package com.gestion_laboratorios.laboratorios.mapper;

import org.springframework.stereotype.Component;

import com.gestion_laboratorios.laboratorios.DTO.AsignacionDTO;
import com.gestion_laboratorios.laboratorios.model.Asignacion;

@Component
public class AsignacionMapper {
    public Asignacion toEntity(AsignacionDTO asignacionDTO) {
        Asignacion asignacion = new Asignacion();
        asignacion.setId(asignacionDTO.getId());
        asignacion.setLaboratorioId(asignacionDTO.getLaboratorioId());
        asignacion.setUsuarioId(asignacionDTO.getUsuarioId());
        asignacion.setNombrePaciente(asignacionDTO.getNombrePaciente());
        asignacion.setApellidoPaciente(asignacionDTO.getApellidoPaciente());
        asignacion.setFechaAsignacion(asignacionDTO.getFechaAsignacion());
        asignacion.setTipoPrueba(asignacionDTO.getTipoPrueba());
        return asignacion;
    }
}
