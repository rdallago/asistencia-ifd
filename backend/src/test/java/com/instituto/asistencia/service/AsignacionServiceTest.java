package com.instituto.asistencia.service;

import com.instituto.asistencia.dto.AsignacionRequest;
import com.instituto.asistencia.entity.Docente;
import com.instituto.asistencia.entity.DocenteTurno;
import com.instituto.asistencia.entity.Turno;
import com.instituto.asistencia.repository.DocenteRepository;
import com.instituto.asistencia.repository.DocenteTurnoRepository;
import com.instituto.asistencia.repository.TurnoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AsignacionServiceTest {

    @Mock
    private DocenteTurnoRepository docenteTurnoRepository;

    @Mock
    private DocenteRepository docenteRepository;

    @Mock
    private TurnoRepository turnoRepository;

    @Mock
    private AuditoriaRespaldoService auditoriaRespaldoService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AsignacionService asignacionService;

    private Docente docenteSample;
    private Turno turnoSample;
    private DocenteTurno asignacionSample;

    @BeforeEach
    void setUp() {
        docenteSample = new Docente();
        docenteSample.setId(1L);
        docenteSample.setNombre("Juan");
        docenteSample.setApellido("Pérez");

        turnoSample = new Turno();
        turnoSample.setId(2L);
        turnoSample.setNombre("Mañana");

        asignacionSample = new DocenteTurno();
        asignacionSample.setId(10L);
        asignacionSample.setDocente(docenteSample);
        asignacionSample.setTurno(turnoSample);
        asignacionSample.setActivo(true);
        asignacionSample.setFechaDesde(LocalDate.now());
    }

    private void mockearSeguridad() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("admin_test");
        SecurityContextHolder.setContext(securityContext);
    }

    // ==========================================
    // TESTS PARA listar() Y listarActivas()
    // ==========================================

    @Test
    void debeListarTodasLasAsignaciones() {
        when(docenteTurnoRepository.findAll()).thenReturn(List.of(asignacionSample));

        List<DocenteTurno> resultado = asignacionService.listar();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(docenteTurnoRepository, times(1)).findAll();
    }

    @Test
    void debeListarSoloAsignacionesActivas() {
        when(docenteTurnoRepository.findByActivoTrue()).thenReturn(List.of(asignacionSample));

        List<DocenteTurno> resultado = asignacionService.listarActivas();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(docenteTurnoRepository, times(1)).findByActivoTrue();
    }

    // ==========================================
    // TESTS PARA crear()
    // ==========================================

    @Test
    void debeCrearAsignacionExitosamente() {
        mockearSeguridad();
        AsignacionRequest request = new AsignacionRequest();
        request.setDocenteId(1L);
        request.setTurnoId(2L);
        request.setFechaDesde(LocalDate.now());

        when(docenteTurnoRepository.existsByDocente_IdAndTurno_IdAndActivoTrue(1L, 2L)).thenReturn(false);
        when(docenteRepository.findById(1L)).thenReturn(Optional.of(docenteSample));
        when(turnoRepository.findById(2L)).thenReturn(Optional.of(turnoSample));
        when(docenteTurnoRepository.save(any(DocenteTurno.class))).thenReturn(asignacionSample);

        DocenteTurno resultado = asignacionService.crear(request);

        assertNotNull(resultado);
        verify(docenteTurnoRepository).save(any(DocenteTurno.class));
        verify(auditoriaRespaldoService).registrar(
                eq("admin_test"),
                eq("ASIGNACIONES"),
                eq("ALTA"),
                eq("EXITOSO"),
                isNull(),
                contains("Pérez, Juan")
        );
    }

    @Test
    void debeLanzarExcepcionAlCrearSiYaExisteAsignacionActiva() {
        AsignacionRequest request = new AsignacionRequest();
        request.setDocenteId(1L);
        request.setTurnoId(2L);

        when(docenteTurnoRepository.existsByDocente_IdAndTurno_IdAndActivoTrue(1L, 2L)).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> asignacionService.crear(request));
        assertEquals("El docente ya se encuentra asignado al turno seleccionado.", ex.getMessage());
    }

    @Test
    void debeLanzarExcepcionAlCrearSiDocenteNoExiste() {
        AsignacionRequest request = new AsignacionRequest();
        request.setDocenteId(1L);
        request.setTurnoId(2L);

        when(docenteTurnoRepository.existsByDocente_IdAndTurno_IdAndActivoTrue(1L, 2L)).thenReturn(false);
        when(docenteRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> asignacionService.crear(request));
        assertEquals("Docente no encontrado", ex.getMessage());
    }

    @Test
    void debeLanzarExcepcionAlCrearSiTurnoNoExiste() {
        AsignacionRequest request = new AsignacionRequest();
        request.setDocenteId(1L);
        request.setTurnoId(2L);

        when(docenteTurnoRepository.existsByDocente_IdAndTurno_IdAndActivoTrue(1L, 2L)).thenReturn(false);
        when(docenteRepository.findById(1L)).thenReturn(Optional.of(docenteSample));
        when(turnoRepository.findById(2L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> asignacionService.crear(request));
        assertEquals("Turno no encontrado", ex.getMessage());
    }

    // ==========================================
    // TESTS PARA desasignar()
    // ==========================================

    @Test
    void debeDesasignarExitosamente() {
        mockearSeguridad();
        when(docenteTurnoRepository.findById(10L)).thenReturn(Optional.of(asignacionSample));
        when(docenteTurnoRepository.save(any(DocenteTurno.class))).thenReturn(asignacionSample);

        asignacionService.desasignar(10L);

        assertFalse(asignacionSample.getActivo());
        assertNotNull(asignacionSample.getFechaHasta());
        verify(auditoriaRespaldoService).registrar(
                eq("admin_test"),
                eq("ASIGNACIONES"),
                eq("BAJA"),
                eq("EXITOSO"),
                isNull(),
                contains("Se quitó la asignación")
        );
    }

    @Test
    void debeLanzarExcepcionAlDesasignarSiNoExiste() {
        when(docenteTurnoRepository.findById(10L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> asignacionService.desasignar(10L));
        assertEquals("Asignación no encontrada", ex.getMessage());
    }

    // ==========================================
    // TESTS PARA reactivar()
    // ==========================================

    @Test
    void debeReactivarExitosamente() {
        mockearSeguridad();
        asignacionSample.setActivo(false);
        asignacionSample.setFechaHasta(LocalDate.now());

        when(docenteTurnoRepository.findById(10L)).thenReturn(Optional.of(asignacionSample));
        when(docenteTurnoRepository.existsByDocente_IdAndTurno_IdAndActivoTrue(1L, 2L)).thenReturn(false);
        when(docenteTurnoRepository.save(any(DocenteTurno.class))).thenReturn(asignacionSample);

        asignacionService.reactivar(10L);

        assertTrue(asignacionSample.getActivo());
        assertNull(asignacionSample.getFechaHasta());
        verify(auditoriaRespaldoService).registrar(
                eq("admin_test"),
                eq("ASIGNACIONES"),
                eq("REACTIVACION"),
                eq("EXITOSO"),
                isNull(),
                contains("Se reactivó la asignación")
        );
    }

    @Test
    void debeLanzarExcepcionAlReactivarSiYaExisteOtraActiva() {
        when(docenteTurnoRepository.findById(10L)).thenReturn(Optional.of(asignacionSample));
        when(docenteTurnoRepository.existsByDocente_IdAndTurno_IdAndActivoTrue(1L, 2L)).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> asignacionService.reactivar(10L));
        assertEquals("El docente ya posee una asignación activa para ese turno", ex.getMessage());
    }
}