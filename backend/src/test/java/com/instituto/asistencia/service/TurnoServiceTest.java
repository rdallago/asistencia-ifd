package com.instituto.asistencia.service;

import com.instituto.asistencia.entity.Institucion;
import com.instituto.asistencia.entity.Turno;
import com.instituto.asistencia.repository.InstitucionRepository;
import com.instituto.asistencia.repository.JornadaRepository;
import com.instituto.asistencia.repository.TurnoRepository;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.Collections;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TurnoServiceTest {

    @Mock
    private TurnoRepository turnoRepository;

    @Mock
    private JornadaRepository jornadaRepository;

    @Mock
    private InstitucionRepository institucionRepository;

    @Mock
    private AuditoriaRespaldoService auditoriaRespaldoService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private TurnoService turnoService;

    private Turno turnoValido;

    @BeforeEach
    void prepararDatos() {

        SecurityContext securityContext =
                SecurityContextHolder.createEmptyContext();

       lenient().when(authentication.getName())
        .thenReturn("usuarioTest");

        securityContext.setAuthentication(authentication);

        SecurityContextHolder.setContext(securityContext);

        turnoValido = new Turno();

        turnoValido.setId(1L);
        turnoValido.setNombre("Turno Noche");
        turnoValido.setHoraInicio(
                LocalTime.of(18, 20)
        );
        turnoValido.setHoraFin(
                LocalTime.of(23, 40)
        );
        turnoValido.setActivo(true);
    }
    @Test
void debeCrearTurnoCorrectamente() {

    Institucion institucion = new Institucion();
    institucion.setId(1L);

    when(institucionRepository.findById(1L))
            .thenReturn(Optional.of(institucion));

    when(turnoRepository.findAll())
            .thenReturn(Arrays.asList());

    when(turnoRepository.save(any(Turno.class)))
            .thenAnswer(invocation ->
                    invocation.getArgument(0));

    Turno resultado =
            turnoService.crear(turnoValido);

    assertNotNull(resultado);

    assertEquals(
            "Turno Noche",
            resultado.getNombre()
    );

    assertEquals(
            LocalTime.of(18, 20),
            resultado.getHoraInicio()
    );

    assertEquals(
            LocalTime.of(23, 40),
            resultado.getHoraFin()
    );

    assertTrue(resultado.getActivo());

    assertEquals(
            institucion,
            resultado.getInstitucion()
    );

    verify(institucionRepository)
            .findById(1L);

    verify(turnoRepository)
            .save(any(Turno.class));

    verify(auditoriaRespaldoService)
            .registrar(
                    eq("usuarioTest"),
                    eq("TURNOS"),
                    eq("ALTA"),
                    eq("EXITOSO"),
                    isNull(),
                    contains("Se creó el turno")
            );
}
@Test
void debeRechazarTurnoCuandoHoraInicioEsPosteriorALaHoraFin() {

    turnoValido.setHoraInicio(
            LocalTime.of(23, 40)
    );

    turnoValido.setHoraFin(
            LocalTime.of(18, 20)
    );

    RuntimeException excepcion =
            assertThrows(
                    RuntimeException.class,
                    () -> turnoService.crear(turnoValido)
            );

    assertEquals(
            "La hora de inicio no puede ser mayor a la hora de fin",
            excepcion.getMessage()
    );

    verify(turnoRepository, never())
            .save(any(Turno.class));

    verify(institucionRepository, never())
            .findById(1L);

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
void debeRechazarTurnoCuandoYaExisteOtroTurnoActivoConElMismoNombre() {

    Turno turnoExistente = new Turno();

    turnoExistente.setId(2L);
    turnoExistente.setNombre("Turno Noche");
    turnoExistente.setHoraInicio(
            LocalTime.of(18, 20)
    );
    turnoExistente.setHoraFin(
            LocalTime.of(23, 40)
    );
    turnoExistente.setActivo(true);

    when(turnoRepository.findAll())
            .thenReturn(
                    Arrays.asList(turnoExistente)
            );

    RuntimeException excepcion =
            assertThrows(
                    RuntimeException.class,
                    () -> turnoService.crear(turnoValido)
            );

    assertEquals(
            "Ya existe un turno activo con ese nombre",
            excepcion.getMessage()
    );

    verify(turnoRepository, never())
            .save(any(Turno.class));

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
void debeRechazarTurnoCuandoNoExisteLaInstitucionConfigurada() {

    when(institucionRepository.findById(1L))
            .thenReturn(Optional.empty());

    when(turnoRepository.findAll())
            .thenReturn(Arrays.asList());

    RuntimeException excepcion =
            assertThrows(
                    RuntimeException.class,
                    () -> turnoService.crear(turnoValido)
            );

    assertEquals(
            "No existe la institución configurada",
            excepcion.getMessage()
    );

    verify(turnoRepository, never())
            .save(any(Turno.class));

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
void debePermitirCrearTurnoCuandoExisteOtroConElMismoNombrePeroEstaInactivo() {

    Turno turnoInactivo = new Turno();

    turnoInactivo.setId(2L);
    turnoInactivo.setNombre("Turno Noche");
    turnoInactivo.setHoraInicio(
            LocalTime.of(18, 20)
    );
    turnoInactivo.setHoraFin(
            LocalTime.of(23, 40)
    );
    turnoInactivo.setActivo(false);

    Institucion institucion = new Institucion();
    institucion.setId(1L);

    when(turnoRepository.findAll())
            .thenReturn(
                    Arrays.asList(turnoInactivo)
            );

    when(institucionRepository.findById(1L))
            .thenReturn(
                    Optional.of(institucion)
            );

    when(turnoRepository.save(any(Turno.class)))
            .thenAnswer(invocation ->
                    invocation.getArgument(0));

    Turno resultado =
            turnoService.crear(turnoValido);

    assertNotNull(resultado);

    assertEquals(
            "Turno Noche",
            resultado.getNombre()
    );

    assertTrue(
            resultado.getActivo()
    );

    verify(turnoRepository)
            .save(any(Turno.class));

    verify(auditoriaRespaldoService)
            .registrar(
                    eq("usuarioTest"),
                    eq("TURNOS"),
                    eq("ALTA"),
                    eq("EXITOSO"),
                    isNull(),
                    contains("Se creó el turno")
            );
}
@Test
void debeModificarTurnoCorrectamente() {

    Turno turnoExistente = new Turno();

    turnoExistente.setId(1L);
    turnoExistente.setNombre("Turno Noche");
    turnoExistente.setHoraInicio(
            LocalTime.of(18, 20)
    );
    turnoExistente.setHoraFin(
            LocalTime.of(23, 40)
    );
    turnoExistente.setActivo(true);

    Turno cambios = new Turno();

    cambios.setNombre("Turno Tarde");
    cambios.setHoraInicio(
            LocalTime.of(13, 30)
    );
    cambios.setHoraFin(
            LocalTime.of(18, 00)
    );
    cambios.setActivo(true);

    when(turnoRepository.findById(1L))
            .thenReturn(Optional.of(turnoExistente));

    when(turnoRepository.findAll())
            .thenReturn(Arrays.asList(turnoExistente));

    when(turnoRepository.save(any(Turno.class)))
            .thenAnswer(invocation ->
                    invocation.getArgument(0));

    Turno resultado =
            turnoService.actualizar(1L, cambios);

    assertNotNull(resultado);

    assertEquals(
            "Turno Tarde",
            resultado.getNombre()
    );

    assertEquals(
            LocalTime.of(13, 30),
            resultado.getHoraInicio()
    );

    assertEquals(
            LocalTime.of(18, 00),
            resultado.getHoraFin()
    );

    assertTrue(resultado.getActivo());

    verify(turnoRepository)
            .save(any(Turno.class));

    verify(auditoriaRespaldoService)
            .registrar(
                    eq("usuarioTest"),
                    eq("TURNOS"),
                    eq("MODIFICACION"),
                    eq("EXITOSO"),
                    isNull(),
                    contains("Se modificó el turno")
            );
}
@Test
void debeRechazarModificacionCuandoTurnoNoExiste() {

    when(turnoRepository.findById(999L))
            .thenReturn(Optional.empty());

    RuntimeException excepcion =
            assertThrows(
                    RuntimeException.class,
                    () -> turnoService.actualizar(
                            999L,
                            turnoValido
                    )
            );

    assertEquals(
            "Turno no encontrado",
            excepcion.getMessage()
    );

    verify(turnoRepository, never())
            .save(any(Turno.class));

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
void debeRechazarDesactivacionCuandoTurnoTieneJornadas() {

    when(turnoRepository.findById(1L))
            .thenReturn(Optional.of(turnoValido));

    when(jornadaRepository.existsByTurno_Id(1L))
            .thenReturn(true);

    RuntimeException excepcion =
            assertThrows(
                    RuntimeException.class,
                    () -> turnoService.desactivar(1L)
            );

    assertEquals(
            "No se puede desactivar un turno que posee jornadas asociadas",
            excepcion.getMessage()
    );

    verify(turnoRepository, never())
            .save(any(Turno.class));

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
void debeDesactivarTurnoCuandoNoTieneJornadas() {

    turnoValido.setActivo(true);

    when(turnoRepository.findById(1L))
            .thenReturn(Optional.of(turnoValido));

    when(jornadaRepository.existsByTurno_Id(1L))
            .thenReturn(false);

    when(turnoRepository.save(any(Turno.class)))
            .thenReturn(turnoValido);

    turnoService.desactivar(1L);

    assertFalse(turnoValido.getActivo());

    verify(turnoRepository)
            .findById(1L);

    verify(jornadaRepository)
            .existsByTurno_Id(1L);

    verify(turnoRepository)
            .save(turnoValido);

    verify(auditoriaRespaldoService)
            .registrar(
                    eq("usuarioTest"),
                    eq("TURNOS"),
                    eq("BAJA"),
                    eq("EXITOSO"),
                    isNull(),
                    contains("Se desactivó el turno")
            );
}
@Test
void debeActivarTurnoCorrectamente() {

    turnoValido.setActivo(false);

    when(turnoRepository.findById(1L))
            .thenReturn(Optional.of(turnoValido));

    turnoService.activar(1L);

    assertTrue(
            turnoValido.getActivo()
    );

    verify(turnoRepository)
            .findById(1L);

    verify(auditoriaRespaldoService)
            .registrar(
                    eq("usuarioTest"),
                    eq("TURNOS"),
                    eq("REACTIVACION"),
                    eq("EXITOSO"),
                    isNull(),
                    contains("Se reactivó el turno")
            );
}
@Test
void debeRechazarDesactivacionCuandoTurnoNoExiste() {

    when(turnoRepository.findById(999L))
            .thenReturn(Optional.empty());

    RuntimeException excepcion =
            assertThrows(
                    RuntimeException.class,
                    () -> turnoService.desactivar(999L)
            );

    assertEquals(
            "Turno no encontrado",
            excepcion.getMessage()
    );

    verify(turnoRepository, never())
            .save(any(Turno.class));

    verify(jornadaRepository, never())
            .existsByTurno_Id(anyLong());

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
void debeRechazarActivacionCuandoTurnoNoExiste() {

    when(turnoRepository.findById(999L))
            .thenReturn(Optional.empty());

    RuntimeException excepcion =
            assertThrows(
                    RuntimeException.class,
                    () -> turnoService.activar(999L)
            );

    assertEquals(
            "Turno no encontrado",
            excepcion.getMessage()
    );

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
void debeListarTodosLosTurnos() {

    Turno turno1 = new Turno();
    turno1.setId(1L);
    turno1.setNombre("Turno Tarde");
    turno1.setActivo(true);

    Turno turno2 = new Turno();
    turno2.setId(2L);
    turno2.setNombre("Turno Noche");
    turno2.setActivo(false);

    when(turnoRepository.findAll())
            .thenReturn(
                    Arrays.asList(turno1, turno2)
            );

    List<Turno> resultado =
            turnoService.listar();

    assertNotNull(resultado);

    assertEquals(
            2,
            resultado.size()
    );

    assertEquals(
            "Turno Tarde",
            resultado.get(0).getNombre()
    );

    assertEquals(
            "Turno Noche",
            resultado.get(1).getNombre()
    );

    verify(turnoRepository)
            .findAll();
}
@Test
void debeListarSolamenteTurnosActivosParaReporte() {

    Turno turno1 = new Turno();
    turno1.setId(1L);
    turno1.setNombre("Turno Tarde");
    turno1.setActivo(true);

    Turno turno2 = new Turno();
    turno2.setId(2L);
    turno2.setNombre("Turno Noche");
    turno2.setActivo(true);

    when(turnoRepository.findByActivoTrue())
            .thenReturn(
                    Arrays.asList(turno1, turno2)
            );

    List<Turno> resultado =
            turnoService.listarParaReporte();

    assertNotNull(resultado);

    assertEquals(
            2,
            resultado.size()
    );

    assertTrue(
            resultado.stream()
                    .allMatch(Turno::getActivo)
    );

    verify(turnoRepository)
            .findByActivoTrue();
}
@Test
void debePermitirModificarTurnoConservandoSuMismoNombre() {

    Turno turnoExistente = new Turno();

    turnoExistente.setId(1L);
    turnoExistente.setNombre("Turno Noche");
    turnoExistente.setHoraInicio(
            LocalTime.of(18, 20)
    );
    turnoExistente.setHoraFin(
            LocalTime.of(23, 40)
    );
    turnoExistente.setActivo(true);

    Turno cambios = new Turno();

    cambios.setNombre("Turno Noche");
    cambios.setHoraInicio(
            LocalTime.of(18, 30)
    );
    cambios.setHoraFin(
            LocalTime.of(23, 50)
    );
    cambios.setActivo(true);

    when(turnoRepository.findById(1L))
            .thenReturn(
                    Optional.of(turnoExistente)
            );

    when(turnoRepository.findAll())
            .thenReturn(
                    Arrays.asList(turnoExistente)
            );

    when(turnoRepository.save(any(Turno.class)))
            .thenAnswer(invocation ->
                    invocation.getArgument(0)
            );

    Turno resultado =
            turnoService.actualizar(
                    1L,
                    cambios
            );

    assertNotNull(resultado);

    assertEquals(
            "Turno Noche",
            resultado.getNombre()
    );

    assertEquals(
            LocalTime.of(18, 30),
            resultado.getHoraInicio()
    );

    assertEquals(
            LocalTime.of(23, 50),
            resultado.getHoraFin()
    );

    verify(turnoRepository)
            .save(any(Turno.class));

    verify(auditoriaRespaldoService)
            .registrar(
                    eq("usuarioTest"),
                    eq("TURNOS"),
                    eq("MODIFICACION"),
                    eq("EXITOSO"),
                    isNull(),
                    contains("Se modificó el turno")
            );
}
@Test
void debeRechazarTurnoAunqueElNombreUseMayusculasDiferentes() {

    Turno turnoExistente = new Turno();

    turnoExistente.setId(1L);
    turnoExistente.setNombre("Turno Noche");
    turnoExistente.setHoraInicio(
            LocalTime.of(18, 20)
    );
    turnoExistente.setHoraFin(
            LocalTime.of(23, 40)
    );
    turnoExistente.setActivo(true);

    Turno nuevo = new Turno();

    nuevo.setNombre("TURNO NOCHE");
    nuevo.setHoraInicio(
            LocalTime.of(18, 20)
    );
    nuevo.setHoraFin(
            LocalTime.of(23, 40)
    );
    nuevo.setActivo(true);

    when(turnoRepository.findAll())
            .thenReturn(
                    Arrays.asList(turnoExistente)
            );

    RuntimeException excepcion =
            assertThrows(
                    RuntimeException.class,
                    () -> turnoService.crear(nuevo)
            );

    assertEquals(
            "Ya existe un turno activo con ese nombre",
            excepcion.getMessage()
    );

    verify(turnoRepository, never())
            .save(any(Turno.class));

    verify(institucionRepository, never())
            .findById(1L);
}
@Test
void debeRechazarModificacionCuandoNuevoNombreYaExiste() {

    Turno turnoExistente = new Turno();

    turnoExistente.setId(1L);
    turnoExistente.setNombre("Turno Tarde");
    turnoExistente.setHoraInicio(
            LocalTime.of(13, 30)
    );
    turnoExistente.setHoraFin(
            LocalTime.of(18, 00)
    );
    turnoExistente.setActivo(true);

    Turno otroTurno = new Turno();

    otroTurno.setId(2L);
    otroTurno.setNombre("Turno Noche");
    otroTurno.setHoraInicio(
            LocalTime.of(18, 20)
    );
    otroTurno.setHoraFin(
            LocalTime.of(23, 40)
    );
    otroTurno.setActivo(true);

    Turno cambios = new Turno();

    cambios.setNombre("Turno Noche");
    cambios.setHoraInicio(
            LocalTime.of(13, 30)
    );
    cambios.setHoraFin(
            LocalTime.of(18, 00)
    );
    cambios.setActivo(true);

    when(turnoRepository.findById(1L))
            .thenReturn(
                    Optional.of(turnoExistente)
            );

    when(turnoRepository.findAll())
            .thenReturn(
                    Arrays.asList(
                            turnoExistente,
                            otroTurno
                    )
            );

    RuntimeException excepcion =
            assertThrows(
                    RuntimeException.class,
                    () -> turnoService.actualizar(
                            1L,
                            cambios
                    )
            );

    assertEquals(
            "Ya existe un turno activo con ese nombre",
            excepcion.getMessage()
    );

    verify(turnoRepository, never())
            .save(any(Turno.class));
}
@Test
void debePermitirModificarTurnoManteniendoSuPropioNombre() {

    Turno turnoExistente = new Turno();

    turnoExistente.setId(1L);
    turnoExistente.setNombre("Turno Tarde");
    turnoExistente.setHoraInicio(
            LocalTime.of(13, 30)
    );
    turnoExistente.setHoraFin(
            LocalTime.of(18, 00)
    );
    turnoExistente.setActivo(true);

    Turno cambios = new Turno();

    cambios.setNombre("Turno Tarde");
    cambios.setHoraInicio(
            LocalTime.of(14, 00)
    );
    cambios.setHoraFin(
            LocalTime.of(18, 30)
    );
    cambios.setActivo(true);

    when(turnoRepository.findById(1L))
            .thenReturn(
                    Optional.of(turnoExistente)
            );

    when(turnoRepository.findAll())
            .thenReturn(
                    Arrays.asList(turnoExistente)
            );

    when(turnoRepository.save(any(Turno.class)))
            .thenAnswer(invocation ->
                    invocation.getArgument(0)
            );

    Turno resultado =
            turnoService.actualizar(
                    1L,
                    cambios
            );

    assertNotNull(resultado);

    assertEquals(
            "Turno Tarde",
            resultado.getNombre()
    );

    assertEquals(
            LocalTime.of(14, 00),
            resultado.getHoraInicio()
    );

    assertEquals(
            LocalTime.of(18, 30),
            resultado.getHoraFin()
    );

    verify(turnoRepository)
            .save(any(Turno.class));

    verify(auditoriaRespaldoService)
            .registrar(
                    eq("usuarioTest"),
                    eq("TURNOS"),
                    eq("MODIFICACION"),
                    eq("EXITOSO"),
                    isNull(),
                    contains("Se modificó el turno")
            );
}
@Test
void debeRechazarActualizacionCuandoSeIntentaDesactivarTurnoConJornadas() {

    Turno turnoExistente = new Turno();

    turnoExistente.setId(1L);
    turnoExistente.setNombre("Turno Noche");
    turnoExistente.setHoraInicio(
            LocalTime.of(18, 20)
    );
    turnoExistente.setHoraFin(
            LocalTime.of(23, 40)
    );
    turnoExistente.setActivo(true);

    Turno cambios = new Turno();

    cambios.setNombre("Turno Noche");
    cambios.setHoraInicio(
            LocalTime.of(18, 20)
    );
    cambios.setHoraFin(
            LocalTime.of(23, 40)
    );
    cambios.setActivo(false);

    when(turnoRepository.findById(1L))
            .thenReturn(
                    Optional.of(turnoExistente)
            );

    when(jornadaRepository.existsByTurno_Id(1L))
            .thenReturn(true);

    RuntimeException excepcion =
            assertThrows(
                    RuntimeException.class,
                    () -> turnoService.actualizar(
                            1L,
                            cambios
                    )
            );

    assertEquals(
            "No se puede desactivar un turno que posee jornadas asociadas",
            excepcion.getMessage()
    );

    verify(turnoRepository, never())
            .save(any(Turno.class));
}
@Test
void debeRegistrarAuditoriaAlReactivarTurno() {

    turnoValido.setActivo(false);

    when(turnoRepository.findById(1L))
            .thenReturn(
                    Optional.of(turnoValido)
            );

    turnoService.activar(1L);

    assertTrue(
            turnoValido.getActivo()
    );

    verify(auditoriaRespaldoService)
            .registrar(
                    eq("usuarioTest"),
                    eq("TURNOS"),
                    eq("REACTIVACION"),
                    eq("EXITOSO"),
                    isNull(),
                    contains(
                            "Se reactivó el turno"
                    )
            );
}

@Test
void debeRechazarCreacionCuandoNoExisteInstitucionConfigurada() {

    turnoValido.setNombre("Turno Noche");
    turnoValido.setHoraInicio(
            LocalTime.of(18, 20)
    );
    turnoValido.setHoraFin(
            LocalTime.of(23, 40)
    );
    turnoValido.setActivo(true);

    when(turnoRepository.findAll())
            .thenReturn(Arrays.asList());

    when(institucionRepository.findById(1L))
            .thenReturn(Optional.empty());

    RuntimeException excepcion =
            assertThrows(
                    RuntimeException.class,
                    () -> turnoService.crear(turnoValido)
            );

    assertEquals(
            "No existe la institución configurada",
            excepcion.getMessage()
    );

    verify(turnoRepository, never())
            .save(any(Turno.class));

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
void debeRechazarCreacionDeTurnoDuplicadoActivo() {

    Turno existente = new Turno();

    existente.setId(1L);
    existente.setNombre("Turno Noche");
    existente.setHoraInicio(
            LocalTime.of(18, 20)
    );
    existente.setHoraFin(
            LocalTime.of(23, 40)
    );
    existente.setActivo(true);

    Turno nuevo = new Turno();

    nuevo.setNombre("Turno Noche");
    nuevo.setHoraInicio(
            LocalTime.of(18, 20)
    );
    nuevo.setHoraFin(
            LocalTime.of(23, 40)
    );
    nuevo.setActivo(true);

    when(turnoRepository.findAll())
            .thenReturn(
                    Arrays.asList(existente)
            );

    RuntimeException excepcion =
            assertThrows(
                    RuntimeException.class,
                    () -> turnoService.crear(nuevo)
            );

    assertEquals(
            "Ya existe un turno activo con ese nombre",
            excepcion.getMessage()
    );

    verify(turnoRepository, never())
            .save(any(Turno.class));

    verify(institucionRepository, never())
            .findById(1L);
}
@Test
void debeDevolverListaVaciaCuandoNoHayTurnos() {

    when(turnoRepository.findAll())
            .thenReturn(
                    Collections.emptyList()
            );

    List<Turno> resultado =
            turnoService.listar();

    assertNotNull(resultado);

    assertTrue(
            resultado.isEmpty()
    );

    verify(turnoRepository)
            .findAll();
}
@Test
void debeDevolverListaVaciaCuandoNoHayTurnosActivos() {

    when(turnoRepository.findByActivoTrue())
            .thenReturn(
                    Collections.emptyList()
            );

    List<Turno> resultado =
            turnoService.listarParaReporte();

    assertNotNull(resultado);

    assertTrue(
            resultado.isEmpty()
    );

    verify(turnoRepository)
            .findByActivoTrue();
}
@Test
void debePersistirDesactivacionDeTurnoCuandoNoTieneJornadas() {

    turnoValido.setActivo(true);

    when(turnoRepository.findById(1L))
            .thenReturn(Optional.of(turnoValido));

    when(jornadaRepository.existsByTurno_Id(1L))
            .thenReturn(false);

    when(turnoRepository.save(any(Turno.class)))
            .thenReturn(turnoValido);

    turnoService.desactivar(1L);

    assertFalse(turnoValido.getActivo());

    verify(turnoRepository)
            .save(turnoValido);

    verify(auditoriaRespaldoService)
            .registrar(
                    eq("usuarioTest"),
                    eq("TURNOS"),
                    eq("BAJA"),
                    eq("EXITOSO"),
                    isNull(),
                    contains("Se desactivó el turno")
            );
}
}