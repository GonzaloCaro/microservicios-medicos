package com.gestion_usuarios.usuarios.mapper;

import org.springframework.stereotype.Component;

import com.gestion_usuarios.usuarios.DTO.UsuarioDTO;
import com.gestion_usuarios.usuarios.model.Usuario;

@Component
public class UsuarioMapper {

    public Usuario toEntity(UsuarioDTO usuarioDTO) {
        Usuario usuario = new Usuario();
        usuario.setId(usuarioDTO.getId());
        usuario.setNombre(usuarioDTO.getNombre());
        usuario.setApellido(usuarioDTO.getApellido());
        usuario.setUserName(usuarioDTO.getUserName());
        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setContrasena(usuarioDTO.getContrasena());

        usuario.setRoleId(usuarioDTO.getRoleId());
        usuario.setAreaId(usuarioDTO.getAreaId());
        return usuario;
    }

    public UsuarioDTO toDTO(Usuario usuario) {
        UsuarioDTO usuarioDTO = new UsuarioDTO();
        usuarioDTO.setId(usuario.getId());
        usuarioDTO.setNombre(usuario.getNombre());
        usuarioDTO.setApellido(usuario.getApellido());
        usuarioDTO.setUserName(usuario.getUserName());
        usuarioDTO.setEmail(usuario.getEmail());
        usuarioDTO.setContrasena(usuario.getContrasena());
        usuarioDTO.setRoleId(usuario.getRoleId());
        usuarioDTO.setAreaId(usuario.getAreaId());
        return usuarioDTO;
    }
}
