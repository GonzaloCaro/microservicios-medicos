package com.gestion_usuarios.usuarios.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gestion_usuarios.usuarios.model.Area;
import com.gestion_usuarios.usuarios.repository.AreaRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AreaService {

    private final AreaRepository areaRepository;

    public AreaService(AreaRepository areaRepository) {
        this.areaRepository = areaRepository;
    }

    public List<Area> getAllAreas() {
        log.info("Obteniendo todas las áreas");
        return areaRepository.findAll();
    }

    @Transactional
    public Area createArea(Area area) {
        log.info("Creando área: {}", area.getNombre());
        return areaRepository.save(area);
    }

    @Transactional
    public Area getAreaById(UUID id) {
        log.info("Obteniendo el área con ID: {}", id);
        return areaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Área no encontrada con ID: " + id));
    }

    @Transactional
    public Area updateArea(UUID id, Area areaDetails) {
        log.info("Actualizando el área con ID: {}", id);
        Area area = areaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Área no encontrada con ID: " + id));

        area.setNombre(areaDetails.getNombre());

        return areaRepository.save(area);
    }

    @Transactional
    public void deleteArea(UUID id) {
        log.info("Eliminando el área con ID: {}", id);
        areaRepository.deleteById(id);
    }

}
