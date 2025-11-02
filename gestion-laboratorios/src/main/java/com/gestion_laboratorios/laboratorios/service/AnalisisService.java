package com.gestion_laboratorios.laboratorios.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.gestion_laboratorios.laboratorios.DTO.AnalisisDTO;
import com.gestion_laboratorios.laboratorios.model.Analisis;
import com.gestion_laboratorios.laboratorios.repository.AnalisisRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AnalisisService {

    private final AnalisisRepository analisisRepository;

    public AnalisisService(AnalisisRepository analisisRepository) {
        this.analisisRepository = analisisRepository;
    }

    public List<Analisis> getAllAnalisis() {
        log.info("Obteniendo todos los análisis");
        return analisisRepository.findAll();
    }

    @Transactional
    public Analisis createAnalisis(AnalisisDTO analisisDTO) {
        log.info("Creando un nuevo análisis");
        Analisis analisis = new Analisis();
        analisis.setCodigo(analisisDTO.getCodigo());
        analisis.setNombre(analisisDTO.getNombre());
        return analisisRepository.save(analisis);
    }

    @Transactional
    public Analisis getAnalisisById(UUID id) {
        log.info("Obteniendo el análisis con ID: {}", id);
        return analisisRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Análisis no encontrado con ID: " + id));
    }

    @Transactional
    public Analisis updateAnalisis(UUID id, Analisis analisis) {
        log.info("Actualizando el análisis con ID: {}", id);
        Analisis existingAnalisis = analisisRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Análisis no encontrado con ID: " + id));
        existingAnalisis.setCodigo(analisis.getCodigo());
        existingAnalisis.setNombre(analisis.getNombre());
        return analisisRepository.save(existingAnalisis);
    }

    @Transactional
    public void deleteAnalisis(UUID id) {
        log.info("Eliminando el análisis con ID: {}", id);
        Analisis analisis = analisisRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Análisis no encontrado con ID: " + id));
        analisisRepository.delete(analisis);
    }

}
