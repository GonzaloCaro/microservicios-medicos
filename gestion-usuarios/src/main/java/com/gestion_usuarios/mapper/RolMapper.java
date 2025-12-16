package com.gestion_usuarios.mapper;

import org.springframework.stereotype.Component;

import com.gestion_usuarios.DTO.RolDTO;
import com.gestion_usuarios.model.Rol;

@Component
public class RolMapper {
    public RolDTO toDTO(Rol rol) {
        RolDTO rolDTO = new RolDTO();
        rolDTO.setId(rol.getId());
        rolDTO.setNombre(rol.getNombre());
        return rolDTO;
    }

    public Rol toEntity(RolDTO rolDTO) {
        Rol rol = new Rol();
        rol.setId(rolDTO.getId());
        rol.setNombre(rolDTO.getNombre());
        return rol;
    }
}
