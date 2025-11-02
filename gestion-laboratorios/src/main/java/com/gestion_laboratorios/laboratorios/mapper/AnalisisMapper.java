package com.gestion_laboratorios.laboratorios.mapper;

import org.springframework.stereotype.Component;

import com.gestion_laboratorios.laboratorios.DTO.AnalisisDTO;
import com.gestion_laboratorios.laboratorios.model.Analisis;

@Component
public class AnalisisMapper {
    public Analisis toEntity(AnalisisDTO analisisDTO) {
        Analisis analisis = new Analisis();
        analisis.setId(analisisDTO.getId());
        analisis.setCodigo(analisisDTO.getCodigo());
        analisis.setNombre(analisisDTO.getNombre());
        return analisis;
    }
}

