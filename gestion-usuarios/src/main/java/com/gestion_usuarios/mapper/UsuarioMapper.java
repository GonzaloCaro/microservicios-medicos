package com.gestion_usuarios.mapper;

import org.springframework.stereotype.Component;

import com.gestion_usuarios.DTO.UsuarioDTO;
import com.gestion_usuarios.model.Area;
import com.gestion_usuarios.model.Rol;
import com.gestion_usuarios.model.RoleUser;
import com.gestion_usuarios.model.Usuario;

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

        if (usuarioDTO.getRoleId() != null && usuarioDTO.getAreaId() != null) {
            RoleUser roleUser = new RoleUser();

            Rol rol = new Rol();
            rol.setId(usuarioDTO.getRoleId());
            roleUser.setRol(rol);

            Area area = new Area();
            area.setId(usuarioDTO.getAreaId());
            roleUser.setArea(area);

            roleUser.setUsuario(usuario);
            usuario.setRole(roleUser);
        }
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
        return usuarioDTO;
    }
}
