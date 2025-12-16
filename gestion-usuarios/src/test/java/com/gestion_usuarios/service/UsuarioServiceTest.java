package com.gestion_usuarios.service;

import com.gestion_usuarios.encoder.PasswordEncoder;
import com.gestion_usuarios.model.Rol;
import com.gestion_usuarios.model.Usuario;
import com.gestion_usuarios.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        // Podríamos mockear el encoder si queremos resultados predecibles
    }

    // --- getAllUsuarios ---
    @Test
    void getAllUsuarios_DeberiaRetornarLista() {
        Usuario u1 = new Usuario();
        Usuario u2 = new Usuario();
        when(usuarioRepository.findAll()).thenReturn(Arrays.asList(u1, u2));

        List<Usuario> lista = usuarioService.getAllUsuarios();

        assertEquals(2, lista.size());
        verify(usuarioRepository, times(1)).findAll();
    }

    // --- createUsuario ---
    @Test
    void createUsuario_DeberiaGuardarUsuario() {
        Usuario u = new Usuario();
        u.setContrasena("1234");

        Usuario saved = new Usuario();
        saved.setContrasena("encoded1234");

        when(usuarioRepository.save(any(Usuario.class))).thenReturn(saved);

        Usuario result = usuarioService.createUsuario(u);

        assertNotNull(result);
        assertEquals(saved.getContrasena(), result.getContrasena());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    // --- getUsuarioById ---
    @Test
    void getUsuarioById_DeberiaRetornarUsuario_CuandoExiste() {
        UUID id = UUID.randomUUID();
        Usuario u = new Usuario();
        u.setId(id);

        when(usuarioRepository.findById(id)).thenReturn(Optional.of(u));

        Usuario result = usuarioService.getUsuarioById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    @Test
    void getUsuarioById_DeberiaLanzarExcepcion_CuandoNoExiste() {
        UUID id = UUID.randomUUID();
        when(usuarioRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> usuarioService.getUsuarioById(id));
        assertEquals("Usuario no encontrado con ID: " + id, ex.getMessage());
    }

    // --- updateUsuario ---
    @Test
    void updateUsuario_DeberiaActualizarCampos() {
        UUID id = UUID.randomUUID();
        Usuario existing = new Usuario();
        existing.setId(id);
        existing.setNombre("Old");

        Usuario update = new Usuario();
        update.setNombre("New");

        when(usuarioRepository.findById(id)).thenReturn(Optional.of(existing));
        when(usuarioRepository.save(existing)).thenReturn(existing);

        Usuario result = usuarioService.updateUsuario(id, update);

        assertEquals("New", result.getNombre());
        verify(usuarioRepository).save(existing);
    }

    // --- deleteUsuario ---
    @Test
    void deleteUsuario_DeberiaLlamarDeleteById() {
        UUID id = UUID.randomUUID();

        usuarioService.deleteUsuario(id);

        verify(usuarioRepository, times(1)).deleteById(id);
    }

    @Test
    void loadUserByUsername_DeberiaLanzarExcepcion_CuandoNoExiste() {
        when(usuarioRepository.findByUserName("notfound")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> usuarioService.loadUserByUsername("notfound"));
    }
}
