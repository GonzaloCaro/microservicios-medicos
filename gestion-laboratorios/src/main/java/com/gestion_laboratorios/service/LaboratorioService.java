package com.gestion_laboratorios.service;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gestion_laboratorios.model.Laboratorio;
import com.gestion_laboratorios.repository.LaboratorioRepository;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class LaboratorioService {

    private final LaboratorioRepository laboratorioRepository;

    public LaboratorioService(LaboratorioRepository laboratorioRepository) {
        this.laboratorioRepository = laboratorioRepository;
    }

    public List<Laboratorio> getAllLaboratorios() {
        log.info("Obteniendo todos los laboratorios");
        return laboratorioRepository.findAll();
    }

    @Transactional
    public Laboratorio createLaboratorio(Laboratorio laboratorio) {
        log.info("Creando un nuevo laboratorio: {}", laboratorio);
        return laboratorioRepository.save(laboratorio);
    }

    @Transactional
    public Laboratorio getLaboratorioById(UUID id) {
        log.info("Obteniendo el laboratorio con ID: {}", id);
        return laboratorioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Laboratorio no encontrado con ID: " + id));
    }

    @Transactional
    public Laboratorio updateLaboratorio(UUID id, Laboratorio laboratorioDetails) {
        log.info("Actualizando el laboratorio con ID: {}", id);
        Laboratorio laboratorio = laboratorioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Laboratorio no encontrado con ID: " + id));

        laboratorio.setNombre(laboratorioDetails.getNombre());
        laboratorio.setUbicacion(laboratorioDetails.getUbicacion());

        return laboratorioRepository.save(laboratorio);
    }

    @Transactional
    public void deleteLaboratorio(UUID id) {
        log.info("Eliminando el laboratorio con ID: {}", id);
        laboratorioRepository.deleteById(id);
    }
}
