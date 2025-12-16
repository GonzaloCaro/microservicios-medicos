package com.gestion_usuarios.mapper;

import org.springframework.stereotype.Component;

import com.gestion_usuarios.DTO.AreaDTO;
import com.gestion_usuarios.model.Area;

@Component
public class AreaMapper {

    public Area toEntity(AreaDTO areaDTO) {
        Area area = new Area();
        area.setId(areaDTO.getId());
        area.setNombre(areaDTO.getNombre());
        return area;
    }

    public AreaDTO toDTO(Area area) {
        AreaDTO areaDTO = new AreaDTO();
        areaDTO.setId(area.getId());
        areaDTO.setNombre(area.getNombre());
        return areaDTO;
    }

}
