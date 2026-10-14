package com.instituto.asistencia.service;

import com.instituto.asistencia.entity.DocenteTurno;
import com.instituto.asistencia.repository.DocenteRepository;
import com.instituto.asistencia.repository.DocenteTurnoRepository;
import com.instituto.asistencia.repository.TurnoRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Arrays;
import java.util.List;
import com.instituto.asistencia.dto.AsignacionRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.time.LocalDate;
import org.springframework.security.core.context.SecurityContext;
import com.instituto.asistencia.entity.Turno;
import java.util.Optional;
import com.instituto.asistencia.entity.Docente;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DocenteTurnoServiceTest {

    @Mock
    private DocenteTurnoRepository docenteTurnoRepository;

    @Mock
    private DocenteRepository docenteRepository;

    @Mock
    private TurnoRepository turnoRepository;

    @Mock
    private AuditoriaRespaldoService auditoriaRespaldoService;

    @InjectMocks
    private DocenteTurnoService docenteTurnoService;

    @Test
    void debeListarTodasLasAsignaciones() {

        DocenteTurno asignacion1 = new DocenteTurno();
        DocenteTurno asignacion2 = new DocenteTurno();

        List<DocenteTurno> asignaciones =
                Arrays.asList(asignacion1, asignacion2);

        when(docenteTurnoRepository.findAll())
                .thenReturn(asignaciones);

        List<DocenteTurno> resultado =
                docenteTurnoService.listar();

        assertEquals(2, resultado.size());
        assertSame(asignacion1, resultado.get(0));
        assertSame(asignacion2, resultado.get(1));

        verify(docenteTurnoRepository)
                .findAll();
    }
    @Test
void debeAsignarDocenteATurnoCorrectamente() {

    AsignacionRequest request = new AsignacionRequest();
    request.setDocenteId(1L);
    request.setTurnoId(2L);
    request.setFechaDesde(
            LocalDate.of(2026, 8, 1)
    );

    Docente docente = new Docente();
    docente.setId(1L);
    docente.setNombre("Juan");
    docente.setApellido("Pérez");

    Turno turno = new Turno();
    turno.setId(2L);
    turno.setNombre("Mañana");

    DocenteTurno guardada = new DocenteTurno();

    when(docenteTurnoRepository
            .existsByDocente_IdAndTurno_Id(1L, 2L))
            .thenReturn(false);

    when(docenteRepository.findById(1L))
            .thenReturn(Optional.of(docente));

    when(turnoRepository.findById(2L))
            .thenReturn(Optional.of(turno));

    when(docenteTurnoRepository.save(any(DocenteTurno.class)))
            .thenReturn(guardada);

    // Mock de autenticación
    Authentication authentication =
            mock(Authentication.class);

    when(authentication.getName())
            .thenReturn("admin");

    SecurityContext securityContext =
            mock(SecurityContext.class);

    when(securityContext.getAuthentication())
            .thenReturn(authentication);

    SecurityContextHolder.setContext(securityContext);

    try {

        DocenteTurno resultado =
                docenteTurnoService.asignar(request);

        assertSame(guardada, resultado);

        verify(docenteTurnoRepository)
                .existsByDocente_IdAndTurno_Id(1L, 2L);

        verify(docenteRepository)
                .findById(1L);

        verify(turnoRepository)
                .findById(2L);

        verify(docenteTurnoRepository)
                .save(argThat(asignacion ->
                        asignacion.getDocente() == docente
                        && asignacion.getTurno() == turno
                        && asignacion.getActivo()
                        && asignacion.getFechaDesde()
                            .equals(
                                LocalDate.of(2026, 8, 1)
                            )
                ));

        verify(auditoriaRespaldoService)
                .registrar(
                        eq("admin"),
                        eq("ASIGNACIONES"),
                        eq("ALTA"),
                        eq("EXITOSO"),
                        isNull(),
                        contains(
                                "Se asignó Pérez, Juan al turno Mañana"
                        )
                );

    } finally {
        SecurityContextHolder.clearContext();
    }
}
@Test
void debeRechazarAsignacionDuplicada() {

    AsignacionRequest request = new AsignacionRequest();
    request.setDocenteId(1L);
    request.setTurnoId(2L);

    when(docenteTurnoRepository
            .existsByDocente_IdAndTurno_Id(1L, 2L))
            .thenReturn(true);

    RuntimeException excepcion =
            assertThrows(
                    RuntimeException.class,
                    () -> docenteTurnoService.asignar(request)
            );

    assertEquals(
            "El docente ya está asignado a ese turno",
            excepcion.getMessage()
    );

    verify(docenteTurnoRepository)
            .existsByDocente_IdAndTurno_Id(1L, 2L);

    verify(docenteRepository, never())
            .findById(anyLong());

    verify(turnoRepository, never())
            .findById(anyLong());

    verify(docenteTurnoRepository, never())
            .save(any(DocenteTurno.class));

    verify(auditoriaRespaldoService, never())
            .registrar(
                    anyString(),
                    anyString(),
                    anyString(),
                    anyString(),
                    any(),
                    anyString()
            );
}

@Test
void debeRechazarAsignacionCuandoElDocenteNoExiste() {

    AsignacionRequest request = new AsignacionRequest();
    request.setDocenteId(99L);
    request.setTurnoId(2L);

    when(docenteTurnoRepository
            .existsByDocente_IdAndTurno_Id(99L, 2L))
            .thenReturn(false);

    when(docenteRepository.findById(99L))
            .thenReturn(Optional.empty());

    RuntimeException excepcion =
            assertThrows(
                    RuntimeException.class,
                    () -> docenteTurnoService.asignar(request)
            );

    assertEquals(
            "Docente no encontrado",
            excepcion.getMessage()
    );

    verify(docenteRepository)
            .findById(99L);

    verify(turnoRepository, never())
            .findById(anyLong());

    verify(docenteTurnoRepository, never())
            .save(any(DocenteTurno.class));

    verify(auditoriaRespaldoService, never())
            .registrar(
                    anyString(),
                    anyString(),
                    anyString(),
                    anyString(),
                    any(),
                    anyString()
            );
}
@Test
void debeRechazarAsignacionCuandoElTurnoNoExiste() {

    AsignacionRequest request = new AsignacionRequest();
    request.setDocenteId(1L);
    request.setTurnoId(99L);

    Docente docente = new Docente();
    docente.setId(1L);
    docente.setNombre("Juan");
    docente.setApellido("Pérez");

    when(docenteTurnoRepository
            .existsByDocente_IdAndTurno_Id(1L, 99L))
            .thenReturn(false);

    when(docenteRepository.findById(1L))
            .thenReturn(Optional.of(docente));

    when(turnoRepository.findById(99L))
            .thenReturn(Optional.empty());

    RuntimeException excepcion =
            assertThrows(
                    RuntimeException.class,
                    () -> docenteTurnoService.asignar(request)
            );

    assertEquals(
            "Turno no encontrado",
            excepcion.getMessage()
    );

    verify(docenteRepository)
            .findById(1L);

    verify(turnoRepository)
            .findById(99L);

    verify(docenteTurnoRepository, never())
            .save(any(DocenteTurno.class));

    verify(auditoriaRespaldoService, never())
            .registrar(
                    anyString(),
                    anyString(),
                    anyString(),
                    anyString(),
                    any(),
                    anyString()
            );
}
@Test
void debeUsarFechaActualCuandoFechaDesdeEsNula() {

    AsignacionRequest request = new AsignacionRequest();
    request.setDocenteId(1L);
    request.setTurnoId(2L);
    request.setFechaDesde(null);

    Docente docente = new Docente();
    docente.setId(1L);
    docente.setNombre("Juan");
    docente.setApellido("Pérez");

    Turno turno = new Turno();
    turno.setId(2L);
    turno.setNombre("Mañana");

    when(docenteTurnoRepository
            .existsByDocente_IdAndTurno_Id(1L, 2L))
            .thenReturn(false);

    when(docenteRepository.findById(1L))
            .thenReturn(Optional.of(docente));

    when(turnoRepository.findById(2L))
            .thenReturn(Optional.of(turno));

    when(docenteTurnoRepository.save(any(DocenteTurno.class)))
            .thenAnswer(invocation ->
                    invocation.getArgument(0));

    Authentication authentication =
            mock(Authentication.class);

    when(authentication.getName())
            .thenReturn("admin");

    SecurityContext securityContext =
            mock(SecurityContext.class);

    when(securityContext.getAuthentication())
            .thenReturn(authentication);

    SecurityContextHolder.setContext(securityContext);

    try {

        DocenteTurno resultado =
                docenteTurnoService.asignar(request);

        assertEquals(
                LocalDate.now(),
                resultado.getFechaDesde()
        );

        assertTrue(resultado.getActivo());

        verify(docenteTurnoRepository)
                .save(any(DocenteTurno.class));

    } finally {
        SecurityContextHolder.clearContext();
    }
}

@Test
void debeRespetarFechaDesdeInformadaEnLaSolicitud() {

    LocalDate fechaDesde =
            LocalDate.of(2025, 3, 15);

    AsignacionRequest request = new AsignacionRequest();
    request.setDocenteId(1L);
    request.setTurnoId(2L);
    request.setFechaDesde(fechaDesde);

    Docente docente = new Docente();
    docente.setId(1L);
    docente.setNombre("Ana");
    docente.setApellido("Gómez");

    Turno turno = new Turno();
    turno.setId(2L);
    turno.setNombre("Tarde");

    when(docenteTurnoRepository
            .existsByDocente_IdAndTurno_Id(1L, 2L))
            .thenReturn(false);

    when(docenteRepository.findById(1L))
            .thenReturn(Optional.of(docente));

    when(turnoRepository.findById(2L))
            .thenReturn(Optional.of(turno));

    when(docenteTurnoRepository.save(any(DocenteTurno.class)))
            .thenAnswer(invocation ->
                    invocation.getArgument(0));

    Authentication authentication =
            mock(Authentication.class);

    when(authentication.getName())
            .thenReturn("admin");

    SecurityContext securityContext =
            mock(SecurityContext.class);

    when(securityContext.getAuthentication())
            .thenReturn(authentication);

    SecurityContextHolder.setContext(securityContext);

    try {

        DocenteTurno resultado =
                docenteTurnoService.asignar(request);

        assertEquals(
                fechaDesde,
                resultado.getFechaDesde()
        );

    } finally {
        SecurityContextHolder.clearContext();
    }
}
@Test
void debeFinalizarAsignacionCorrectamente() {

    DocenteTurno asignacion =
            new DocenteTurno();

    asignacion.setActivo(true);
    asignacion.setFechaDesde(
            LocalDate.of(2026, 1, 1)
    );

    when(docenteTurnoRepository.findById(1L))
            .thenReturn(Optional.of(asignacion));

    when(docenteTurnoRepository.save(asignacion))
            .thenReturn(asignacion);

    docenteTurnoService.finalizar(1L);

    assertFalse(
            asignacion.getActivo()
    );

    assertEquals(
            LocalDate.now(),
            asignacion.getFechaHasta()
    );

    verify(docenteTurnoRepository)
            .findById(1L);

    verify(docenteTurnoRepository)
            .save(asignacion);
}
@Test
void debeRechazarFinalizacionCuandoLaAsignacionNoExiste() {

    when(docenteTurnoRepository.findById(999L))
            .thenReturn(Optional.empty());

    RuntimeException excepcion =
            assertThrows(
                    RuntimeException.class,
                    () -> docenteTurnoService.finalizar(999L)
            );

    assertEquals(
            "Asignación no encontrada",
            excepcion.getMessage()
    );

    verify(docenteTurnoRepository)
            .findById(999L);

    verify(docenteTurnoRepository, never())
            .save(any(DocenteTurno.class));
}
    @Test
void debeCrearAsignacionActivaConDatosCorrectos() {

    AsignacionRequest request = new AsignacionRequest();
    request.setDocenteId(1L);
    request.setTurnoId(2L);
    request.setFechaDesde(
            LocalDate.of(2026, 8, 20)
    );

    Docente docente = new Docente();
    docente.setId(1L);
    docente.setNombre("Juan");
    docente.setApellido("Pérez");

    Turno turno = new Turno();
    turno.setId(2L);
    turno.setNombre("Turno noche");

    when(docenteTurnoRepository
            .existsByDocente_IdAndTurno_Id(1L, 2L))
            .thenReturn(false);

    when(docenteRepository.findById(1L))
            .thenReturn(Optional.of(docente));

    when(turnoRepository.findById(2L))
            .thenReturn(Optional.of(turno));

    when(docenteTurnoRepository
            .save(any(DocenteTurno.class)))
            .thenAnswer(invocation ->
                    invocation.getArgument(0));

    Authentication authentication =
            mock(Authentication.class);

    when(authentication.getName())
            .thenReturn("admin");

    SecurityContext securityContext =
            mock(SecurityContext.class);

    when(securityContext.getAuthentication())
            .thenReturn(authentication);

    SecurityContextHolder.setContext(securityContext);

    try {

        DocenteTurno resultado =
                docenteTurnoService.asignar(request);

        assertNotNull(resultado);

        assertSame(
                docente,
                resultado.getDocente()
        );

        assertSame(
                turno,
                resultado.getTurno()
        );

        assertTrue(
                resultado.getActivo()
        );

        assertEquals(
                LocalDate.of(2026, 8, 20),
                resultado.getFechaDesde()
        );

        assertNull(
                resultado.getFechaHasta()
        );

    } finally {
        SecurityContextHolder.clearContext();
    }
}
@Test
void debeMantenerFechaDesdeAlFinalizarAsignacion() {

    LocalDate fechaDesde =
            LocalDate.of(2026, 2, 10);

    DocenteTurno asignacion =
            new DocenteTurno();

    asignacion.setId(1L);
    asignacion.setActivo(true);
    asignacion.setFechaDesde(fechaDesde);

    when(docenteTurnoRepository
            .findById(1L))
            .thenReturn(Optional.of(asignacion));

    when(docenteTurnoRepository
            .save(asignacion))
            .thenReturn(asignacion);

    docenteTurnoService.finalizar(1L);

    assertFalse(
            asignacion.getActivo()
    );

    assertEquals(
            fechaDesde,
            asignacion.getFechaDesde()
    );

    assertEquals(
            LocalDate.now(),
            asignacion.getFechaHasta()
    );

    verify(docenteTurnoRepository)
            .findById(1L);

    verify(docenteTurnoRepository)
            .save(asignacion);
}
}