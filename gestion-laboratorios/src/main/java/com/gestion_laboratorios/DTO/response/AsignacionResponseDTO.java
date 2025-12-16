package com.gestion_laboratorios.DTO.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.gestion_laboratorios.DTO.AnalisisDTO;
import com.gestion_laboratorios.DTO.LaboratorioDTO;
import com.gestion_laboratorios.DTO.PacienteDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AsignacionResponseDTO {
    private UUID id;
    private UUID usuarioId;
    private LocalDateTime fechaAsignacion;
    private String detalle;

    private AnalisisDTO analisis;
    private PacienteDTO paciente;
    private LaboratorioDTO laboratorio;
}