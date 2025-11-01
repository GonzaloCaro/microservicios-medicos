package com.gestion_laboratorios.laboratorios.DTO;

import java.time.LocalDateTime;
import java.util.UUID;

public class AsignacionDTO {

    private UUID id;

    private UUID laboratorioId;
    private UUID usuarioId;

    private String nombrePaciente;
    private String apellidoPaciente;
    private LocalDateTime fechaAsignacion;
    private String tipoPrueba;

    // Getters y Setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getLaboratorioId() {
        return laboratorioId;
    }

    public void setLaboratorioId(UUID laboratorioId) {
        this.laboratorioId = laboratorioId;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(UUID usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getNombrePaciente() {
        return nombrePaciente;
    }

    public void setNombrePaciente(String nombrePaciente) {
        this.nombrePaciente = nombrePaciente;
    }

    public String getApellidoPaciente() {
        return apellidoPaciente;
    }

    public void setApellidoPaciente(String apellidoPaciente) {
        this.apellidoPaciente = apellidoPaciente;
    }

    public LocalDateTime getFechaAsignacion() {
        return fechaAsignacion;
    }

    public void setFechaAsignacion(LocalDateTime fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }

    public String getTipoPrueba() {
        return tipoPrueba;
    }

    public void setTipoPrueba(String tipoPrueba) {
        this.tipoPrueba = tipoPrueba;
    }

}
