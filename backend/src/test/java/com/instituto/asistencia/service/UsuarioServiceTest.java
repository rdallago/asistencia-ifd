package com.instituto.asistencia.service;

import com.instituto.asistencia.dto.UsuarioRequest;
import com.instituto.asistencia.dto.UsuarioResponse;
import com.instituto.asistencia.entity.Docente;
import com.instituto.asistencia.entity.Usuario;
import com.instituto.asistencia.entity.enums.Rol;
import com.instituto.asistencia.repository.DocenteRepository;
import com.instituto.asistencia.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepo;

    @Mock
    private DocenteRepository docenteRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuditoriaRespaldoService auditoriaRespaldoService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuarioSample;
    private Docente docenteSample;
    private UsuarioRequest requestSample;

    @BeforeEach
    void setUp() {
        docenteSample = new Docente();
        docenteSample.setId(10L);
        docenteSample.setNombre("Carlos");
        docenteSample.setApellido("Gómez");

        usuarioSample = new Usuario();
        usuarioSample.setId(1L);
        usuarioSample.setUsername("cgomez");
        usuarioSample.setPassword("encoded_pass");
        usuarioSample.setNombre("Carlos");
        usuarioSample.setApellido("Gómez");
        usuarioSample.setEmail("carlos@mail.com");
        usuarioSample.setRol(Rol.ADMIN);
        usuarioSample.setActivo(true);
        usuarioSample.setDocente(docenteSample);

        requestSample = new UsuarioRequest();
        requestSample.setUsername("cgomez");
        requestSample.setPassword("123456");
        requestSample.setNombre("Carlos");
        requestSample.setApellido("Gómez");
        requestSample.setEmail("carlos@mail.com");
        requestSample.setRol("ADMIN");
        requestSample.setDocenteId(10L);
    }

    private void mockearSeguridad() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("admin_test");
        SecurityContextHolder.setContext(securityContext);
    }

    // ==========================================
    // TESTS PARA crear()
    // ==========================================

    @Test
    void debeCrearUsuarioExitosamente() {
        mockearSeguridad();
        when(usuarioRepo.existsByUsername("cgomez")).thenReturn(false);
        when(usuarioRepo.existsByEmail("carlos@mail.com")).thenReturn(false);
        when(usuarioRepo.existsByDocenteId(10L)).thenReturn(false);
        when(docenteRepo.findById(10L)).thenReturn(Optional.of(docenteSample));
        when(passwordEncoder.encode("123456")).thenReturn("encoded_pass");
        when(usuarioRepo.save(any(Usuario.class))).thenReturn(usuarioSample);

        UsuarioResponse response = usuarioService.crear(requestSample);

        assertNotNull(response);
        assertEquals("cgomez", response.getUsername());
        assertEquals("Gómez, Carlos", response.getDocenteNombre());
        verify(auditoriaRespaldoService).registrar(
                eq("admin_test"), eq("USUARIOS"), eq("ALTA"), eq("EXITOSO"), isNull(), contains("cgomez")
        );
    }

    @Test
    void debeLanzarExcepcionSiUsernameExiste() {
        when(usuarioRepo.existsByUsername("cgomez")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> usuarioService.crear(requestSample));
        assertEquals("Ya existe un usuario con ese nombre", ex.getMessage());
    }

    @Test
    void debeLanzarExcepcionSiEmailExiste() {
        when(usuarioRepo.existsByUsername("cgomez")).thenReturn(false);
        when(usuarioRepo.existsByEmail("carlos@mail.com")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> usuarioService.crear(requestSample));
        assertEquals("Ya existe un usuario con ese correo", ex.getMessage());
    }

    @Test
    void debeLanzarExcepcionSiDocenteYaTieneUsuario() {
        when(usuarioRepo.existsByUsername("cgomez")).thenReturn(false);
        when(usuarioRepo.existsByEmail("carlos@mail.com")).thenReturn(false);
        when(usuarioRepo.existsByDocenteId(10L)).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> usuarioService.crear(requestSample));
        assertEquals("El docente ya tiene un usuario asociado", ex.getMessage());
    }

    // ==========================================
    // TESTS PARA BÚSQUEDAS Y LISTADOS
    // ==========================================

    @Test
    void debeListarUsuarios() {
        when(usuarioRepo.findAll()).thenReturn(List.of(usuarioSample));

        List<UsuarioResponse> lista = usuarioService.listar();

        assertEquals(1, lista.size());
        assertEquals("cgomez", lista.get(0).getUsername());
    }

    @Test
    void debeBuscarPorIdExitosamente() {
        when(usuarioRepo.findById(1L)).thenReturn(Optional.of(usuarioSample));

        UsuarioResponse response = usuarioService.buscarPorId(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void debeBuscarPorTexto() {
        when(usuarioRepo.findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCaseOrUsernameContainingIgnoreCase(
                "carlos", "carlos", "carlos")).thenReturn(List.of(usuarioSample));

        List<UsuarioResponse> lista = usuarioService.buscar("carlos");

        assertEquals(1, lista.size());
        assertEquals("cgomez", lista.get(0).getUsername());
    }

    // ==========================================
    // TESTS PARA actualizar()
    // ==========================================

    @Test
    void debeActualizarUsuarioExitosamente() {
        mockearSeguridad();
        when(usuarioRepo.findById(1L)).thenReturn(Optional.of(usuarioSample));
        when(docenteRepo.findById(10L)).thenReturn(Optional.of(docenteSample));
        when(passwordEncoder.encode("123456")).thenReturn("new_encoded_pass");
        when(usuarioRepo.save(any(Usuario.class))).thenReturn(usuarioSample);

        UsuarioResponse response = usuarioService.actualizar(1L, requestSample);

        assertNotNull(response);
        verify(auditoriaRespaldoService).registrar(
                eq("admin_test"), eq("USUARIOS"), eq("MODIFICACION"), eq("EXITOSO"), isNull(), contains("cgomez")
        );
    }

    // ==========================================
    // TESTS PARA desactivar() Y activar()
    // ==========================================

    @Test
    void debeDesactivarUsuario() {
        mockearSeguridad();
        when(usuarioRepo.findById(1L)).thenReturn(Optional.of(usuarioSample));
        when(usuarioRepo.save(any(Usuario.class))).thenReturn(usuarioSample);

        usuarioService.desactivar(1L);

        assertFalse(usuarioSample.getActivo());
        verify(auditoriaRespaldoService).registrar(
                eq("admin_test"), eq("USUARIOS"), eq("BAJA"), eq("EXITOSO"), isNull(), contains("cgomez")
        );
    }

    @Test
    void debeActivarUsuario() {
        mockearSeguridad();
        usuarioSample.setActivo(false);
        when(usuarioRepo.findById(1L)).thenReturn(Optional.of(usuarioSample));
        when(usuarioRepo.save(any(Usuario.class))).thenReturn(usuarioSample);

        usuarioService.activar(1L);

        assertTrue(usuarioSample.getActivo());
        verify(auditoriaRespaldoService).registrar(
                eq("admin_test"), eq("USUARIOS"), eq("REACTIVACION"), eq("EXITOSO"), isNull(), contains("cgomez")
        );
    }
    // ==========================================
    // EXCEPCIONES Y CASOS BORDE EN actualizar()
    // ==========================================

    @Test
    void debeLanzarExcepcionAlActualizarSiUsuarioNoExiste() {
        when(usuarioRepo.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, 
                () -> usuarioService.actualizar(99L, requestSample));
        assertEquals("Usuario no encontrado", ex.getMessage());
    }

    @Test
    void debeLanzarExcepcionAlActualizarSiDocenteYaTieneUsuarioAsociado() {
        // Simular que el usuario actual no tiene docente (o tiene uno distinto)
        usuarioSample.setDocente(null);

        when(usuarioRepo.findById(1L)).thenReturn(Optional.of(usuarioSample));
        when(usuarioRepo.existsByDocenteId(10L)).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, 
                () -> usuarioService.actualizar(1L, requestSample));
        assertEquals("El docente ya tiene un usuario asociado", ex.getMessage());
    }

    @Test
    void debeActualizarSinModificarPasswordSiVieneVacia() {
        mockearSeguridad();
        requestSample.setPassword(""); // Clave vacía

        when(usuarioRepo.findById(1L)).thenReturn(Optional.of(usuarioSample));
        when(docenteRepo.findById(10L)).thenReturn(Optional.of(docenteSample));
        when(usuarioRepo.save(any(Usuario.class))).thenReturn(usuarioSample);

        UsuarioResponse response = usuarioService.actualizar(1L, requestSample);

        assertNotNull(response);
        // Verificamos que NUNCA se llamó al encoder para la contraseña
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void debeQuitarDocenteAlActualizarSiDocenteIdEsNull() {
        mockearSeguridad();
        requestSample.setDocenteId(null);

        when(usuarioRepo.findById(1L)).thenReturn(Optional.of(usuarioSample));
        when(usuarioRepo.save(any(Usuario.class))).thenReturn(usuarioSample);

        UsuarioResponse response = usuarioService.actualizar(1L, requestSample);

        assertNotNull(response);
        assertNull(usuarioSample.getDocente());
    }
}