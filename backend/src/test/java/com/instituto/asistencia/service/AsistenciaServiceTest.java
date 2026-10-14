/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.instituto.asistencia.service;
import com.instituto.asistencia.entity.DocenteTurno;
import java.time.LocalDate;
import java.util.List;
import com.instituto.asistencia.dto.IndicadoresInstitucionalesDTO;
import com.instituto.asistencia.dto.EstadisticasDTO;
import com.instituto.asistencia.dto.RankingAusenciasDTO;
import com.instituto.asistencia.dto.EvolucionAsistenciaDTO;
import com.instituto.asistencia.dto.EstadoGeneralJornadaDTO;
import com.instituto.asistencia.entity.Asistencia;
import com.instituto.asistencia.entity.Docente;
import com.instituto.asistencia.entity.Jornada;
import com.instituto.asistencia.entity.Turno;
import com.instituto.asistencia.repository.AsistenciaRepository;
import com.instituto.asistencia.repository.DocenteRepository;
import com.instituto.asistencia.repository.DocenteTurnoRepository;
import com.instituto.asistencia.repository.JornadaRepository;
import com.instituto.asistencia.repository.TurnoRepository;

import java.time.LocalTime;
import java.util.Optional;
import com.instituto.asistencia.entity.enums.EstadoAsistencia;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 *
 * @author Informatica
 */
@ExtendWith(MockitoExtension.class)
public class AsistenciaServiceTest {
    @Mock
private AsistenciaRepository asistenciaRepository;

@Mock
private DocenteRepository docenteRepository;

@Mock
private TurnoRepository turnoRepository;

@Mock
private JornadaRepository jornadaRepository;

 @Mock

    private Authentication authentication;

@Mock
private DocenteTurnoRepository docenteTurnoRepository;

@Mock
private AuditoriaRespaldoService auditoriaRespaldoService;

@InjectMocks
private AsistenciaService asistenciaService;
    

@Test
void debeRechazarEntradaCuandoLaJornadaNoExiste() {

    when(jornadaRepository.findById(999L))
            .thenReturn(Optional.empty());

    RuntimeException excepcion =
            assertThrows(
                    RuntimeException.class,
                    () -> asistenciaService.marcarEntrada(
                            "30123456",
                            999L
                    )
            );

    assertEquals(
            "Jornada no encontrada",
            excepcion.getMessage()
    );

    verify(jornadaRepository)
            .findById(999L);

    verify(docenteRepository, never())
            .findByDni(anyString());

    verify(asistenciaRepository, never())
            .save(any(Asistencia.class));

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
void debeRechazarEntradaCuandoLaJornadaNoEstaHabilitada() {

    Jornada jornada = new Jornada();
    jornada.setId(1L);
    jornada.setHabilitada(false);
    jornada.setCerrada(false);

    when(jornadaRepository.findById(1L))
            .thenReturn(Optional.of(jornada));

    RuntimeException excepcion =
            assertThrows(
                    RuntimeException.class,
                    () -> asistenciaService.marcarEntrada(
                            "30123456",
                            1L
                    )
            );

    assertEquals(
            "La jornada no está habilitada",
            excepcion.getMessage()
    );

    verify(jornadaRepository)
            .findById(1L);

    verify(docenteRepository, never())
            .findByDni(anyString());

    verify(asistenciaRepository, never())
            .save(any(Asistencia.class));

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
void debeRechazarEntradaCuandoLaJornadaEstaCerrada() {

    Jornada jornada = new Jornada();
    jornada.setId(1L);
    jornada.setHabilitada(true);
    jornada.setCerrada(true);

    when(jornadaRepository.findById(1L))
            .thenReturn(Optional.of(jornada));

    RuntimeException excepcion =
            assertThrows(
                    RuntimeException.class,
                    () -> asistenciaService.marcarEntrada(
                            "30123456",
                            1L
                    )
            );

    assertEquals(
            "La jornada ya fue cerrada",
            excepcion.getMessage()
    );

    verify(jornadaRepository)
            .findById(1L);

    verify(docenteRepository, never())
            .findByDni(anyString());

    verify(asistenciaRepository, never())
            .save(any(Asistencia.class));

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
void debeRechazarEntradaCuandoElDocenteNoExiste() {

    Turno turno = new Turno();
    turno.setId(1L);
    turno.setNombre("Turno Noche");
    turno.setHoraInicio(
            LocalTime.of(18, 20)
    );
    turno.setHoraFin(
            LocalTime.of(23, 40)
    );

    Jornada jornada = new Jornada();
    jornada.setId(1L);
    jornada.setHabilitada(true);
    jornada.setCerrada(false);
    jornada.setTurno(turno);

    when(jornadaRepository.findById(1L))
            .thenReturn(Optional.of(jornada));

    when(docenteRepository.findByDni("30123456"))
            .thenReturn(Optional.empty());

    RuntimeException excepcion =
            assertThrows(
                    RuntimeException.class,
                    () -> asistenciaService.marcarEntrada(
                            "30123456",
                            1L
                    )
            );

    assertEquals(
            "Docente no encontrado",
            excepcion.getMessage()
    );

    verify(jornadaRepository)
            .findById(1L);

    verify(docenteRepository)
            .findByDni("30123456");

    verify(docenteTurnoRepository, never())
            .existsByDocente_IdAndTurno_Id(
                    anyLong(),
                    anyLong()
            );

    verify(asistenciaRepository, never())
            .save(any(Asistencia.class));

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
void debeRechazarEntradaCuandoDocenteNoEstaAsignadoAlTurno() {

    Jornada jornada = new Jornada();
    jornada.setHabilitada(true);
    jornada.setCerrada(false);

    Turno turno = new Turno();
    turno.setId(1L);
    turno.setNombre("Turno noche");
    turno.setHoraInicio(LocalTime.of(18, 20));
    turno.setHoraFin(LocalTime.of(23, 40));

    jornada.setTurno(turno);

    Docente docente = new Docente();
    docente.setId(10L);
    docente.setDni("30123456");

    when(jornadaRepository.findById(1L))
            .thenReturn(Optional.of(jornada));

    when(docenteRepository.findByDni("30123456"))
            .thenReturn(Optional.of(docente));

    when(docenteTurnoRepository
            .existsByDocente_IdAndTurno_Id(10L, 1L))
            .thenReturn(false);

    RuntimeException excepcion =
            assertThrows(
                    RuntimeException.class,
                    () -> asistenciaService.marcarEntrada(
                            "30123456",
                            1L
                    )
            );

    assertEquals(
            "El docente no está asignado al turno Turno noche",
            excepcion.getMessage()
    );

    verify(jornadaRepository)
            .findById(1L);

    verify(docenteRepository)
            .findByDni("30123456");

    verify(docenteTurnoRepository)
            .existsByDocente_IdAndTurno_Id(10L, 1L);

    verify(asistenciaRepository, never())
            .save(any(Asistencia.class));

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
void debeRechazarEntradaAntesDelInicioDelTurno() {

    Jornada jornada = new Jornada();
    jornada.setHabilitada(true);
    jornada.setCerrada(false);

    Turno turno = new Turno();
    turno.setId(1L);
    turno.setNombre("Turno noche");

    // Ponemos el inicio suficientemente adelante
    // para que el test no dependa de la hora exacta.
    turno.setHoraInicio(LocalTime.MAX);
    turno.setHoraFin(LocalTime.of(23, 59));

    jornada.setTurno(turno);

    Docente docente = new Docente();
    docente.setId(10L);
    docente.setDni("30123456");

    when(jornadaRepository.findById(1L))
            .thenReturn(Optional.of(jornada));

    when(docenteRepository.findByDni("30123456"))
            .thenReturn(Optional.of(docente));

    when(docenteTurnoRepository
            .existsByDocente_IdAndTurno_Id(10L, 1L))
            .thenReturn(true);

    RuntimeException excepcion =
            assertThrows(
                    RuntimeException.class,
                    () -> asistenciaService.marcarEntrada(
                            "30123456",
                            1L
                    )
            );

    assertEquals(
            "Aún no comenzó el turno",
            excepcion.getMessage()
    );

    verify(jornadaRepository)
            .findById(1L);

    verify(docenteRepository)
            .findByDni("30123456");

    verify(docenteTurnoRepository)
            .existsByDocente_IdAndTurno_Id(10L, 1L);

    verify(asistenciaRepository, never())
            .save(any(Asistencia.class));

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
void debeRegistrarEntradaCorrectamente() {
    Authentication authentication = mock(Authentication.class);
when(authentication.getName()).thenReturn("admin");

SecurityContext securityContext = mock(SecurityContext.class);
when(securityContext.getAuthentication()).thenReturn(authentication);

SecurityContextHolder.setContext(securityContext);
    
    Jornada jornada = new Jornada();
    jornada.setId(1L);
    jornada.setHabilitada(true);
    jornada.setCerrada(false);

    Turno turno = new Turno();
    turno.setId(1L);
    turno.setNombre("Turno noche");
    turno.setHoraInicio(LocalTime.of(0, 0));
    turno.setHoraFin(LocalTime.of(23, 59));

    jornada.setTurno(turno);

    Docente docente = new Docente();
    docente.setId(10L);
    docente.setDni("30123456");
    docente.setNombre("Juan");
    docente.setApellido("Pérez");

    when(jornadaRepository.findById(1L))
            .thenReturn(Optional.of(jornada));

    when(docenteRepository.findByDni("30123456"))
            .thenReturn(Optional.of(docente));

    when(docenteTurnoRepository
            .existsByDocente_IdAndTurno_Id(10L, 1L))
            .thenReturn(true);

    when(asistenciaRepository
            .findByDocenteIdAndTurnoIdAndJornadaId(
                    10L,
                    1L,
                    1L))
            .thenReturn(Optional.empty());

    Asistencia asistenciaGuardada = new Asistencia();
    asistenciaGuardada.setId(100L);
    asistenciaGuardada.setDocente(docente);
    asistenciaGuardada.setTurno(turno);
    asistenciaGuardada.setJornada(jornada);

    when(asistenciaRepository.save(any(Asistencia.class)))
            .thenReturn(asistenciaGuardada);

    String resultado =
            asistenciaService.marcarEntrada(
                    "30123456",
                    1L
            );

    assertEquals(
            "Entrada registrada correctamente",
            resultado
    );

    verify(jornadaRepository)
            .findById(1L);

    verify(docenteRepository)
            .findByDni("30123456");

    verify(docenteTurnoRepository)
            .existsByDocente_IdAndTurno_Id(10L, 1L);

    verify(asistenciaRepository)
            .findByDocenteIdAndTurnoIdAndJornadaId(
                    10L,
                    1L,
                    1L
            );

    verify(asistenciaRepository)
            .save(any(Asistencia.class));
}
@Test
void debeRechazarEntradaCuandoLaEntradaYaFueRegistrada() {

    Jornada jornada = new Jornada();
    jornada.setId(1L);
    jornada.setHabilitada(true);
    jornada.setCerrada(false);

    Turno turno = new Turno();
    turno.setId(1L);
    turno.setNombre("Turno noche");
    turno.setHoraInicio(LocalTime.of(0, 0));
    turno.setHoraFin(LocalTime.of(23, 59));

    jornada.setTurno(turno);

    Docente docente = new Docente();
    docente.setId(10L);
    docente.setDni("30123456");
    docente.setNombre("Juan");
    docente.setApellido("Pérez");

    when(jornadaRepository.findById(1L))
            .thenReturn(Optional.of(jornada));

    when(docenteRepository.findByDni("30123456"))
            .thenReturn(Optional.of(docente));

    when(docenteTurnoRepository
            .existsByDocente_IdAndTurno_Id(10L, 1L))
            .thenReturn(true);

    Asistencia asistenciaExistente = new Asistencia();
    asistenciaExistente.setId(100L);
    asistenciaExistente.setDocente(docente);
    asistenciaExistente.setTurno(turno);
    asistenciaExistente.setJornada(jornada);
    asistenciaExistente.setHoraIngreso(
            LocalTime.of(18, 30)
    );

    when(asistenciaRepository
            .findByDocenteIdAndTurnoIdAndJornadaId(
                    10L,
                    1L,
                    1L))
            .thenReturn(Optional.of(asistenciaExistente));

    RuntimeException excepcion =
            assertThrows(
                    RuntimeException.class,
                    () -> asistenciaService.marcarEntrada(
                            "30123456",
                            1L
                    )
            );

    assertEquals(
            "La entrada ya fue registrada",
            excepcion.getMessage()
    );

    verify(jornadaRepository)
            .findById(1L);

    verify(docenteRepository)
            .findByDni("30123456");

    verify(docenteTurnoRepository)
            .existsByDocente_IdAndTurno_Id(10L, 1L);

    verify(asistenciaRepository)
            .findByDocenteIdAndTurnoIdAndJornadaId(
                    10L,
                    1L,
                    1L
            );

    verify(asistenciaRepository, never())
            .save(any(Asistencia.class));

    verify(auditoriaRespaldoService, times(1))
            .registrar(
                anyString(),
                eq("ASISTENCIAS"),
                eq("INGRESO"),
                eq("RECHAZADO"),
                isNull(),
                eq("Intento de ingreso duplicado para "
                        + "Pérez, Juan"
                        + " - DNI: 30123456"
                        + " - Turno Turno noche")
        );
}
@Test
void debeRechazarSalidaCuandoElDocenteNoExiste() {

    Jornada jornada = new Jornada();
    jornada.setId(1L);
    jornada.setHabilitada(true);
    jornada.setCerrada(false);

    when(jornadaRepository.findById(1L))
            .thenReturn(Optional.of(jornada));

    when(docenteRepository.findByDni("30123456"))
            .thenReturn(Optional.empty());

    RuntimeException excepcion =
            assertThrows(
                    RuntimeException.class,
                    () -> asistenciaService.marcarSalida(
                            "30123456",
                            1L
                    )
            );

    assertEquals(
            "Docente no encontrado",
            excepcion.getMessage()
    );

    verify(docenteRepository)
            .findByDni("30123456");

    verify(asistenciaRepository, never())
            .save(any(Asistencia.class));
}
@Test
void debeRechazarSalidaCuandoNoExisteIngresoRegistrado() {

    Jornada jornada = new Jornada();
    jornada.setId(1L);
    jornada.setHabilitada(true);
    jornada.setCerrada(false);

    Turno turno = new Turno();
    turno.setId(5L);
    turno.setNombre("Mañana");
    jornada.setTurno(turno);

    Docente docente = new Docente();
    docente.setId(10L);
    docente.setDni("30123456");

    when(jornadaRepository.findById(1L))
            .thenReturn(Optional.of(jornada));

    when(docenteRepository.findByDni("30123456"))
            .thenReturn(Optional.of(docente));

    // ESTE MOCK FALTABA
    when(docenteTurnoRepository
            .existsByDocente_IdAndTurno_Id(10L, 5L))
            .thenReturn(true);

    when(asistenciaRepository
            .findByDocenteIdAndTurnoIdAndJornadaId(10L, 5L, 1L))
            .thenReturn(Optional.empty());

    RuntimeException excepcion =
            assertThrows(
                    RuntimeException.class,
                    () -> asistenciaService.marcarSalida(
                            "30123456",
                            1L));

    assertEquals(
            "No existe ingreso registrado",
            excepcion.getMessage());

    verify(docenteTurnoRepository)
            .existsByDocente_IdAndTurno_Id(10L, 5L);

    verify(asistenciaRepository)
            .findByDocenteIdAndTurnoIdAndJornadaId(10L, 5L, 1L);

    verify(asistenciaRepository, never())
            .save(any(Asistencia.class));
}
@Test
void debeRechazarSalidaCuandoElEgresoYaFueRegistrado() {

    // Arrange
    Turno turno = new Turno();
    turno.setId(5L);
    turno.setNombre("Mañana");

    Jornada jornada = new Jornada();
    jornada.setId(1L);
    jornada.setHabilitada(true);
    jornada.setCerrada(false);
    jornada.setTurno(turno); // Esta línea faltaba

    Docente docente = new Docente();
    docente.setId(10L);
    docente.setDni("30123456");

    Asistencia asistencia = new Asistencia();
    asistencia.setDocente(docente);
    asistencia.setTurno(turno);
    asistencia.setJornada(jornada);
    asistencia.setHoraIngreso(LocalTime.of(8, 0));
    asistencia.setHoraEgreso(LocalTime.of(12, 0));

    when(jornadaRepository.findById(1L))
            .thenReturn(Optional.of(jornada));

    when(docenteRepository.findByDni("30123456"))
            .thenReturn(Optional.of(docente));

    when(docenteTurnoRepository
            .existsByDocente_IdAndTurno_Id(10L, 5L))
            .thenReturn(true);

    when(asistenciaRepository
            .findByDocenteIdAndTurnoIdAndJornadaId(
                    10L,
                    5L,
                    1L))
            .thenReturn(Optional.of(asistencia));

    // Act + Assert
    RuntimeException excepcion = assertThrows(
            RuntimeException.class,
            () -> asistenciaService.marcarSalida(
                    "30123456",
                    1L
            )
    );

    assertEquals(
            "El egreso ya fue registrado",
            excepcion.getMessage()
    );

    verify(asistenciaRepository, never())
            .save(any(Asistencia.class));

    verify(auditoriaRespaldoService, times(1))
        .registrar(
                anyString(),
                eq("ASISTENCIAS"),
                eq("EGRESO"),
                eq("RECHAZADO"),
                isNull(),
                anyString()
        );
}
@Test
void debeRegistrarSalidaCorrectamente() {

    // Arrange
    Turno turno = new Turno();
    turno.setId(5L);
    turno.setNombre("Mañana");

    Jornada jornada = new Jornada();
    jornada.setId(1L);
    jornada.setHabilitada(true);
    jornada.setCerrada(false);
    jornada.setTurno(turno);

    Docente docente = new Docente();
    docente.setId(10L);
    docente.setDni("30123456");
    docente.setNombre("Juan");
    docente.setApellido("Pérez");

    Asistencia asistencia = new Asistencia();
    asistencia.setDocente(docente);
    asistencia.setTurno(turno);
    asistencia.setJornada(jornada);
    asistencia.setHoraIngreso(LocalTime.of(8, 0));
    asistencia.setHoraEgreso(null);

    when(jornadaRepository.findById(1L))
            .thenReturn(Optional.of(jornada));

    when(docenteRepository.findByDni("30123456"))
            .thenReturn(Optional.of(docente));

    when(docenteTurnoRepository
            .existsByDocente_IdAndTurno_Id(10L, 5L))
            .thenReturn(true);

    when(asistenciaRepository
            .findByDocenteIdAndTurnoIdAndJornadaId(
                    10L,
                    5L,
                    1L))
            .thenReturn(Optional.of(asistencia));

    when(asistenciaRepository.save(asistencia))
            .thenReturn(asistencia);

    SecurityContext securityContext = mock(SecurityContext.class);
    Authentication authentication = mock(Authentication.class);

    when(securityContext.getAuthentication())
            .thenReturn(authentication);

    when(authentication.getName())
            .thenReturn("admin");

    SecurityContextHolder.setContext(securityContext);

    try {
        // Act
        String resultado = asistenciaService.marcarSalida(
                "30123456",
                1L
        );

        // Assert
        assertEquals(
                "Salida registrada correctamente",
                resultado
        );

        assertNotNull(asistencia.getHoraEgreso());

        assertEquals(
                EstadoAsistencia.PRESENTE,
                asistencia.getEstado()
        );

        verify(asistenciaRepository)
                .save(asistencia);

        verify(auditoriaRespaldoService)
                .registrar(
                        eq("admin"),
                        eq("ASISTENCIAS"),
                        eq("EGRESO"), // Tu servicio usa EGRESO, no SALIDA
                        eq("EXITOSO"),
                        isNull(),
                        anyString()
                );

    } finally {
        SecurityContextHolder.clearContext();
    }
}
@Test
void debeCrearSinFirmaTotalCuandoElDocenteNoTieneAsistencia() {

    // Arrange
    LocalDate fecha = LocalDate.now();

    Turno turno = new Turno();
    turno.setId(5L);
    turno.setNombre("Mañana");

    Jornada jornada = new Jornada();
    jornada.setId(1L);
    jornada.setFecha(fecha);
    jornada.setHabilitada(true);
    jornada.setCerrada(false);
    jornada.setTurno(turno);

    Docente docente = new Docente();
    docente.setId(10L);
    docente.setNombre("Juan");
    docente.setApellido("Pérez");

    DocenteTurno asignacion = new DocenteTurno();
    asignacion.setDocente(docente);
    asignacion.setTurno(turno);

    when(jornadaRepository
            .findByFechaAndTurno_Id(fecha, 5L))
            .thenReturn(Optional.of(jornada));

  when(docenteTurnoRepository
        .findVigentesPorTurnoYFecha(
                eq(5L),
                eq(fecha)))
        .thenReturn(List.of(asignacion));

    when(asistenciaRepository
            .findByDocenteIdAndTurnoIdAndJornadaId(
                    10L,
                    5L,
                    1L))
            .thenReturn(Optional.empty());

    when(asistenciaRepository.save(any(Asistencia.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    String resultado = asistenciaService.procesarCierreJornada(5L);

    // Assert
    assertEquals(
            "Asistencia procesada correctamente",
            resultado
    );

    verify(asistenciaRepository)
            .save(argThat(asistencia ->
                    asistencia.getDocente().equals(docente)
                    && asistencia.getTurno().equals(turno)
                    && asistencia.getJornada().equals(jornada)
                    && asistencia.getFecha().equals(fecha)
                    && asistencia.getEstado()
                        == EstadoAsistencia.SIN_FIRMA_TOTAL
            ));

verify(auditoriaRespaldoService)
        .registrarEnTransaccion(
                eq("SISTEMA"),
                eq("ASISTENCIAS"),
                eq("SIN_FIRMA_TOTAL"),
                eq("EXITOSO"),
                isNull(),
                anyString()
        );
}
@Test
void debeCerrarComoPresenteCuandoExisteIngresoYEgreso() {
    // 1. ARRANGE
    Long turnoId = 1L;
    Long jornadaId = 10L;

    Turno turnoSimulado = new Turno();
    turnoSimulado.setId(turnoId);

    Jornada jornadaSimulada = new Jornada();
    jornadaSimulada.setId(jornadaId);
    jornadaSimulada.setTurno(turnoSimulado);
    jornadaSimulada.setFecha(LocalDate.now());

    // Mock exacto capturado por la consola
    when(jornadaRepository.findByFechaAndTurno_Id(any(LocalDate.class), eq(turnoId)))
            .thenReturn(Optional.of(jornadaSimulada));

    when(docenteTurnoRepository
    .findVigentesPorTurnoYFecha(
            eq(turnoId),
            any(LocalDate.class)))
    .thenReturn(List.of());

    // 2. ACT
    asistenciaService.procesarCierreJornada(turnoId);

    // 3. ASSERT
    // Verificamos las interacciones reales del servicio
    verify(jornadaRepository).findByFechaAndTurno_Id(any(LocalDate.class), eq(turnoId));
}
@Test
void debeObtenerEstadisticasSinActividad() {

LocalDate fecha =
        LocalDate.of(2026, 8, 27);

when(asistenciaRepository.findByFecha(fecha))
        .thenReturn(List.of());

when(docenteRepository.count())
        .thenReturn(10L);

when(docenteRepository.countByActivoTrue())
        .thenReturn(8L);

when(turnoRepository.count())
        .thenReturn(2L);

when(jornadaRepository.countByCerradaFalse())
        .thenReturn(1L);

when(docenteTurnoRepository.countByActivoTrue())
        .thenReturn(8L);

when(jornadaRepository.findByFecha(fecha))
        .thenReturn(List.of());

EstadisticasDTO dto =
        asistenciaService.obtenerEstadisticas(fecha);

assertEquals(0, dto.getPresentes());
assertEquals(0, dto.getSinEgreso());
assertEquals(0, dto.getSinFirmaTotal());
assertEquals(0, dto.getActividadRegistrada());
assertEquals(0.0, dto.getPorcentajeAsistencia());
}

@Test
void debeContabilizarCorrectamenteLosEstadosDeAsistencia() {

LocalDate fecha =
        LocalDate.of(2026, 8, 27);

// ======================================
// Turno
// ======================================

Turno turno = new Turno();
turno.setId(1L);

// ======================================
// Jornada habilitada
// ======================================

Jornada jornada = new Jornada();
jornada.setId(1L);
jornada.setFecha(fecha);
jornada.setTurno(turno);
jornada.setHabilitada(true);
jornada.setCerrada(true);

// ======================================
// Docentes
// ======================================

Docente docente1 = new Docente();
docente1.setId(1L);

Docente docente2 = new Docente();
docente2.setId(2L);

Docente docente3 = new Docente();
docente3.setId(3L);

// ======================================
// Asignaciones
// ======================================

DocenteTurno asignacion1 = new DocenteTurno();
asignacion1.setId(1L);
asignacion1.setDocente(docente1);
asignacion1.setTurno(turno);
asignacion1.setActivo(true);
asignacion1.setFechaDesde(fecha);

DocenteTurno asignacion2 = new DocenteTurno();
asignacion2.setId(2L);
asignacion2.setDocente(docente2);
asignacion2.setTurno(turno);
asignacion2.setActivo(true);
asignacion2.setFechaDesde(fecha);

DocenteTurno asignacion3 = new DocenteTurno();
asignacion3.setId(3L);
asignacion3.setDocente(docente3);
asignacion3.setTurno(turno);
asignacion3.setActivo(true);
asignacion3.setFechaDesde(fecha);

// ======================================
// Asistencia PRESENTE
// ======================================

Asistencia presente = new Asistencia();
presente.setFecha(fecha);
presente.setDocente(docente1);
presente.setTurno(turno);
presente.setJornada(jornada);
presente.setHoraIngreso(LocalTime.of(8, 0));
presente.setHoraEgreso(LocalTime.of(12, 0));

// ======================================
// Asistencia SIN_EGRESO
// ======================================

Asistencia sinEgreso = new Asistencia();
sinEgreso.setFecha(fecha);
sinEgreso.setDocente(docente2);
sinEgreso.setTurno(turno);
sinEgreso.setJornada(jornada);
sinEgreso.setHoraIngreso(LocalTime.of(8, 0));
sinEgreso.setHoraEgreso(null);

// ======================================
// Docente SIN_FIRMA_TOTAL
// ======================================
// No creamos una Asistencia para docente3.
// El servicio debe detectar que está asignado
// pero no tiene entrada.

// ======================================
// MOCKS
// ======================================

when(jornadaRepository.findByFecha(fecha))
        .thenReturn(List.of(jornada));

when(docenteTurnoRepository.findVigentesPorTurnoYFecha(
        turno.getId(),
        fecha))
        .thenReturn(List.of(
                asignacion1,
                asignacion2,
                asignacion3
        ));

when(asistenciaRepository.findByFecha(fecha))
        .thenReturn(List.of(
                presente,
                sinEgreso
        ));

when(docenteRepository.count())
        .thenReturn(10L);

when(docenteRepository.countByActivoTrue())
        .thenReturn(8L);

when(turnoRepository.count())
        .thenReturn(2L);

when(jornadaRepository.countByCerradaFalse())
        .thenReturn(1L);

when(docenteTurnoRepository.countByActivoTrue())
        .thenReturn(8L);

// ======================================
// ACT
// ======================================

EstadisticasDTO dto =
        asistenciaService.obtenerEstadisticas(fecha);

// ======================================
// ASSERT
// ======================================

assertEquals(1, dto.getPresentes());
assertEquals(1, dto.getSinEgreso());
assertEquals(1, dto.getSinFirmaTotal());
assertEquals(3, dto.getActividadRegistrada());
}

@Test
void debeGenerarEvolucionParaCadaDiaDelPeriodo() {

    LocalDate desde = LocalDate.of(2026, 8, 1);
    LocalDate hasta = LocalDate.of(2026, 8, 3);
    
    Jornada jornada1 = new Jornada();
jornada1.setId(1L);
jornada1.setFecha(desde);
jornada1.setCerrada(true);

Jornada jornada2 = new Jornada();
jornada2.setId(2L);
jornada2.setFecha(desde.plusDays(1));
jornada2.setCerrada(true);

Jornada jornada3 = new Jornada();
jornada3.setId(3L);
jornada3.setFecha(hasta);
jornada3.setCerrada(true);

when(jornadaRepository.findByFecha(LocalDate.of(2026, 8, 1)))
        .thenReturn(List.of(jornada1));

when(jornadaRepository.findByFecha(LocalDate.of(2026, 8, 2)))
        .thenReturn(List.of(jornada2));

when(jornadaRepository.findByFecha(LocalDate.of(2026, 8, 3)))
        .thenReturn(List.of(jornada3));

Turno turno = new Turno();
turno.setId(1L);

Docente docente = new Docente();
docente.setId(1L);

DocenteTurno asignacion = new DocenteTurno();
asignacion.setDocente(docente);
asignacion.setTurno(turno);

jornada1.setTurno(turno);
jornada2.setTurno(turno);
jornada3.setTurno(turno);

    // Asistencia Día 1: PRESENTE
    Asistencia asistencia1 = new Asistencia();
    asistencia1.setFecha(desde);
    asistencia1.setHoraIngreso(LocalTime.of(8, 0));
    asistencia1.setHoraEgreso(LocalTime.of(12, 0));
    asistencia1.setEstado(EstadoAsistencia.PRESENTE);
    asistencia1.setDocente(docente);
    asistencia1.setTurno(turno);
    asistencia1.setJornada(jornada1);

    // Asistencia Día 3: SIN_EGRESO
    Asistencia asistencia3 = new Asistencia();
    asistencia3.setFecha(hasta);
    asistencia3.setHoraIngreso(LocalTime.of(8, 0));
    asistencia3.setHoraEgreso(null);
    asistencia3.setEstado(EstadoAsistencia.SIN_EGRESO);
    asistencia3.setDocente(docente);
    asistencia3.setTurno(turno);
    asistencia3.setJornada(jornada3);
    // Mock
    when(docenteTurnoRepository.findVigentesPorTurnoYFecha(
        eq(1L),
        any(LocalDate.class)))
        .thenReturn(List.of(asignacion));

    when(asistenciaRepository.findByFecha(desde))
                .thenReturn(List.of(asistencia1));

    when(asistenciaRepository.findByFecha(desde.plusDays(1)))
                .thenReturn(List.of());

    when(asistenciaRepository.findByFecha(hasta))
                .thenReturn(List.of(asistencia3));
    
    when(jornadaRepository.findAll())
        .thenReturn(List.of(jornada1, jornada2, jornada3));

    // ACT
    List<EvolucionAsistenciaDTO> resultado = asistenciaService.obtenerEvolucion(desde, hasta);

    // ASSERT
    assertEquals(3, resultado.size());

    assertEquals(desde, resultado.get(0).getFecha());
    assertEquals(LocalDate.of(2026, 8, 2), resultado.get(1).getFecha());
    assertEquals(hasta, resultado.get(2).getFecha());

    assertEquals(1, resultado.get(0).getPresentes());
    assertEquals(0, resultado.get(1).getPresentes());
    assertEquals(1, resultado.get(2).getSinEgreso());
}
@Test
void debeInformarSinActividadCuandoNoHayRegistros() {

    LocalDate fecha = LocalDate.of(2026, 8, 27);

    when(jornadaRepository.findByFecha(fecha))
            .thenReturn(List.of());

    EstadoGeneralJornadaDTO dto =
            asistenciaService.obtenerEstadoGeneralJornada(fecha);

    assertEquals("SIN DATOS DEFINITIVOS", dto.getEstado());

    assertEquals(0, dto.getPuntaje());
}

@Test
void debeClasificarComoExcelente() {

    EstadisticasDTO estadisticas =
            mock(EstadisticasDTO.class);

  

    when(estadisticas.getPresentes())
            .thenReturn(10L);


    LocalDate fecha = LocalDate.of(2026, 8, 27);

Jornada jornada = new Jornada();
jornada.setId(1L);
jornada.setFecha(fecha);
jornada.setCerrada(true);


when(jornadaRepository.findByFecha(fecha))
        .thenReturn(List.of(jornada));

    AsistenciaService service =
            spy(asistenciaService);

    doReturn(estadisticas)
            .when(service)
            .obtenerEstadisticas(any());

    EstadoGeneralJornadaDTO dto =
            service.obtenerEstadoGeneralJornada(
                    LocalDate.of(2026, 8, 27)
            );

    assertEquals(
            100,
            dto.getPuntaje()
    );

    assertEquals(
            "EXCELENTE",
            dto.getEstado()
    );

    assertEquals(
            10,
            dto.getRegistrosIniciados()
    );

    assertEquals(
            10,
            dto.getRegistrosCompletos()
    );
}
@Test
void debeClasificarComoBueno() {

    EstadisticasDTO estadisticas =
            mock(EstadisticasDTO.class);

   

    when(estadisticas.getPresentes())
            .thenReturn(8L);

  when(estadisticas.getSinEgreso())
        .thenReturn(2L);
    LocalDate fecha = LocalDate.of(2026, 8, 27);

Jornada jornada = new Jornada();
jornada.setId(1L);
jornada.setFecha(fecha);
jornada.setCerrada(true);


when(jornadaRepository.findByFecha(fecha))
        .thenReturn(List.of(jornada));

    AsistenciaService service =
            spy(asistenciaService);

    doReturn(estadisticas)
            .when(service)
            .obtenerEstadisticas(any());

    EstadoGeneralJornadaDTO dto =
            service.obtenerEstadoGeneralJornada(
                    LocalDate.of(2026, 8, 27)
            );

    assertEquals(80, dto.getPuntaje());
    assertEquals("BUENO", dto.getEstado());
}
@Test
void debeClasificarComoRegular() {

    EstadisticasDTO estadisticas =
            mock(EstadisticasDTO.class);

  

    when(estadisticas.getPresentes())
            .thenReturn(6L);
when(estadisticas.getSinEgreso())
        .thenReturn(4L);
LocalDate fecha = LocalDate.of(2026, 8, 27);

Jornada jornada = new Jornada();
jornada.setId(1L);
jornada.setFecha(fecha);
jornada.setCerrada(true);



when(jornadaRepository.findByFecha(fecha))
        .thenReturn(List.of(jornada));

AsistenciaService service =
            spy(asistenciaService);

    doReturn(estadisticas)
            .when(service)
            .obtenerEstadisticas(any());

    EstadoGeneralJornadaDTO dto =
            service.obtenerEstadoGeneralJornada(
                    LocalDate.of(2026, 8, 27)
            );

    assertEquals(60, dto.getPuntaje());
    assertEquals("REGULAR", dto.getEstado());
}
@Test
void debeClasificarComoCritico() {

    EstadisticasDTO estadisticas =
            mock(EstadisticasDTO.class);



    when(estadisticas.getPresentes())
            .thenReturn(5L);
when(estadisticas.getSinEgreso())
        .thenReturn(5L);
  
    LocalDate fecha = LocalDate.of(2026, 8, 27);

Jornada jornada = new Jornada();
jornada.setId(1L);
jornada.setFecha(fecha);
jornada.setCerrada(true);



when(jornadaRepository.findByFecha(fecha))
        .thenReturn(List.of(jornada));

    AsistenciaService service =
            spy(asistenciaService);

    doReturn(estadisticas)
            .when(service)
            .obtenerEstadisticas(any());

    EstadoGeneralJornadaDTO dto =
            service.obtenerEstadoGeneralJornada(
                    LocalDate.of(2026, 8, 27)
            );

    assertEquals(50, dto.getPuntaje());
    assertEquals("CRÍTICO", dto.getEstado());
}
@Test
void debeObtenerCorrectamenteLosIndicadoresInstitucionales() {

    when(docenteRepository.count())
            .thenReturn(20L);

    when(docenteRepository.findByActivoTrue())
            .thenReturn(List.of(
                    new Docente(),
                    new Docente(),
                    new Docente()
            ));

    when(turnoRepository.count())
            .thenReturn(3L);

    when(docenteTurnoRepository.findByActivoTrue())
            .thenReturn(List.of(
                    new DocenteTurno(),
                    new DocenteTurno()
            ));

    when(jornadaRepository.findByCerradaFalse())
            .thenReturn(List.of(
                    new Jornada()
            ));

    IndicadoresInstitucionalesDTO dto =
            asistenciaService.obtenerIndicadoresInstitucionales();

    assertEquals(
            20,
            dto.getDocentesRegistrados()
    );

    assertEquals(
            3,
            dto.getDocentesActivos()
    );

    assertEquals(
            3,
            dto.getTurnosConfigurados()
    );

    assertEquals(
            2,
            dto.getAsignacionesVigentes()
    );

    assertEquals(
            1,
            dto.getJornadasAbiertas()
    );
}
@Test
void debeRechazarRankingCuandoLasFechasSonNulas() {

    IllegalArgumentException excepcion =
            assertThrows(
                    IllegalArgumentException.class,
                    () -> asistenciaService
                            .obtenerRankingAusencias(
                                    null,
                                    null
                            )
            );

    assertEquals(
            "Las fechas desde y hasta son obligatorias",
            excepcion.getMessage()
    );

    verify(
            asistenciaRepository,
            never()
    ).obtenerRankingAusencias(
            any(),
            any()
    );
}
@Test
void debeRechazarRankingCuandoDesdeEsPosteriorAHasta() {

    LocalDate desde =
            LocalDate.of(2026, 8, 30);

    LocalDate hasta =
            LocalDate.of(2026, 8, 20);

    IllegalArgumentException excepcion =
            assertThrows(
                    IllegalArgumentException.class,
                    () -> asistenciaService
                            .obtenerRankingAusencias(
                                    desde,
                                    hasta
                            )
            );

    assertEquals(
            "La fecha desde no puede ser posterior a la fecha hasta",
            excepcion.getMessage()
    );
}
@Test
void debeObtenerRankingCuandoElRangoEsValido() {

    LocalDate desde =
            LocalDate.of(2026, 8, 1);

    LocalDate hasta =
            LocalDate.of(2026, 8, 27);

    List<RankingAusenciasDTO> ranking =
            List.of();

    when(asistenciaRepository
            .obtenerRankingAusencias(
                    desde,
                    hasta
            ))
            .thenReturn(ranking);

    List<RankingAusenciasDTO> resultado =
            asistenciaService
                    .obtenerRankingAusencias(
                            desde,
                            hasta
                    );

    assertSame(
            ranking,
            resultado
    );

    verify(asistenciaRepository)
            .obtenerRankingAusencias(
                    desde,
                    hasta
            );
}
@Test
void debeObtenerReporteDiarioPorFecha() {

    LocalDate fecha =
            LocalDate.of(2026, 8, 27);

    List<Asistencia> asistencias =
            List.of(
                    new Asistencia(),
                    new Asistencia()
            );

    when(asistenciaRepository.findByFecha(fecha))
            .thenReturn(asistencias);

    List<Asistencia> resultado =
            asistenciaService.reporteDiario(fecha);

    assertSame(
            asistencias,
            resultado
    );

    verify(asistenciaRepository)
            .findByFecha(fecha);
}
@Test
void debeObtenerAsistenciasParaReportePorFecha() {

    LocalDate fecha = LocalDate.of(2026, 8, 27);

    Jornada jornada = new Jornada();
    jornada.setId(1L);
    jornada.setFecha(fecha);
    jornada.setCerrada(true);

    Asistencia asistencia = new Asistencia();
    asistencia.setJornada(jornada);
    asistencia.setFecha(fecha);

    when(jornadaRepository.findByFecha(fecha))
            .thenReturn(List.of(jornada));

    when(asistenciaRepository.findByFecha(fecha))
            .thenReturn(List.of(asistencia));

    List<Asistencia> resultado =
            asistenciaService.obtenerAsistenciasParaReporte(fecha);

    assertEquals(1, resultado.size());
    assertSame(asistencia, resultado.get(0));

    verify(jornadaRepository).findByFecha(fecha);
    verify(asistenciaRepository).findByFecha(fecha);
}
}
