package com.gestion_laboratorios.laboratorios.DTO;

import java.time.LocalDateTime;
import java.util.UUID;

public class AsignacionDTO {

    private UUID id;

    private UUID laboratorioId;
    private UUID usuarioId;
    private UUID analisisId;
    private String detalle;
    private LocalDateTime fechaAsignacion;

    // En caso de que exista el paciente
    private UUID pacienteId;

    // En caso de que no exista el paciente
    private String rut;
    private String dv;
    private int edad;
    private String nombrePaciente;
    private String apellidoPaciente;
    private String telefono;

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

    public UUID getAnalisisId() {
        return analisisId;
    }

    public void setAnalisisId(UUID analisisId) {
        this.analisisId = analisisId;
    }

    public UUID getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(UUID pacienteId) {
        this.pacienteId = pacienteId;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public LocalDateTime getFechaAsignacion() {
        return fechaAsignacion;
    }

    public void setFechaAsignacion(LocalDateTime fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }

    // Getters y Setters Paciente
    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

    public String getDv() {
        return dv;
    }

    public void setDv(String dv) {
        this.dv = dv;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
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

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
