package com.gestion_laboratorios.mapper;

import org.springframework.stereotype.Component;

import com.gestion_laboratorios.DTO.LaboratorioDTO;
import com.gestion_laboratorios.model.Laboratorio;

@Component
public class LaboratorioMapper {

    public Laboratorio toEntity(LaboratorioDTO laboratorioDTO) {
        Laboratorio laboratorio = new Laboratorio();
        laboratorio.setId(laboratorioDTO.getId());
        laboratorio.setNombre(laboratorioDTO.getNombre());
        laboratorio.setUbicacion(laboratorioDTO.getUbicacion());
        return laboratorio;
    }

    public LaboratorioDTO toDTO(Laboratorio laboratorio) {
        LaboratorioDTO laboratorioDTO = new LaboratorioDTO();
        laboratorioDTO.setId(laboratorio.getId());
        laboratorioDTO.setNombre(laboratorio.getNombre());
        laboratorioDTO.setUbicacion(laboratorio.getUbicacion());
        return laboratorioDTO;
    }
}
