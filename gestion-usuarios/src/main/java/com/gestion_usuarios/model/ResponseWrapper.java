package com.gestion_usuarios.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "status", "cantidad", "timestamp", "data" })
public class ResponseWrapper<T> {
    private String status;
    private int cantidad;
    private LocalDateTime timestamp;
    private List<T> data;

    public ResponseWrapper(String status, int cantidad, List<T> data) {
        this.status = status;
        this.cantidad = cantidad;
        this.timestamp = LocalDateTime.now();
        this.data = data;
    }

    // Getters
    public String getStatus() {
        return status;
    }

    public int getCantidad() {
        return cantidad;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public List<T> getData() {
        return data;
    }

    // Setters
    public void setStatus(String status) {
        this.status = status;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}