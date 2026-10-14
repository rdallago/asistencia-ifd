package com.instituto.asistencia.service;

import com.instituto.asistencia.entity.Jornada;
import com.instituto.asistencia.entity.Turno;
import com.instituto.asistencia.entity.Usuario;
import com.instituto.asistencia.repository.JornadaRepository;
import com.instituto.asistencia.repository.TurnoRepository;
import com.instituto.asistencia.repository.UsuarioRepository;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JornadaServiceTest {

    @Mock
    private JornadaRepository jornadaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private TurnoRepository turnoRepository;

    @Mock
    private AsistenciaService asistenciaService;

    @Mock
    private AuditoriaRespaldoService auditoriaRespaldoService;
    
    @Mock
    private CierreJornadaIndividualService cierreJornadaIndividualService;

    @InjectMocks
    private JornadaService jornadaService;

    private Usuario usuarioValido;
    private Turno turnoValido;
    private Jornada jornadaValida;

    @BeforeEach
    void prepararDatos() {

        usuarioValido = new Usuario();
        usuarioValido.setId(1L);
        usuarioValido.setUsername("usuarioTest");

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

        jornadaValida = new Jornada();

        jornadaValida.setFecha(LocalDate.now());
        jornadaValida.setTurno(turnoValido);
        jornadaValida.setHabilitada(true);
        jornadaValida.setCerrada(false);
        jornadaValida.setFechaApertura(
                LocalDateTime.now()
        );
        jornadaValida.setUsuarioApertura(
                usuarioValido
        );
    }
    @Test
void debeAbrirJornadaCorrectamente() {

    when(jornadaRepository.existsByFechaAndTurno_Id(
            LocalDate.now(),
            1L
    )).thenReturn(false);

    when(usuarioRepository.findById(1L))
            .thenReturn(Optional.of(usuarioValido));

    when(turnoRepository.findById(1L))
            .thenReturn(Optional.of(turnoValido));

    when(jornadaRepository.save(any(Jornada.class)))
            .thenAnswer(invocation ->
                    invocation.getArgument(0)
            );

    Jornada resultado =
            jornadaService.abrirJornada(
                    1L,
                    1L,
                    "Jornada normal"
            );

    assertNotNull(resultado);

    assertEquals(
            LocalDate.now(),
            resultado.getFecha()
    );

    assertEquals(
            turnoValido,
            resultado.getTurno()
    );

    assertTrue(
            resultado.getHabilitada()
    );

    assertFalse(
            resultado.getCerrada()
    );

    assertEquals(
            usuarioValido,
            resultado.getUsuarioApertura()
    );

    assertEquals(
            "Jornada normal",
            resultado.getObservaciones()
    );

    verify(jornadaRepository)
            .existsByFechaAndTurno_Id(
                    LocalDate.now(),
                    1L
            );

    verify(usuarioRepository)
            .findById(1L);

    verify(turnoRepository)
            .findById(1L);

    verify(jornadaRepository)
            .save(any(Jornada.class));

    verify(auditoriaRespaldoService)
            .registrar(
                    eq("usuarioTest"),
                    eq("JORNADAS"),
                    eq("APERTURA"),
                    eq("EXITOSO"),
                    isNull(),
                    contains("Se abrió la jornada")
            );
}
@Test
void debeRechazarJornadaDuplicadaParaMismoTurno() {

    when(jornadaRepository.existsByFechaAndTurno_Id(
            LocalDate.now(),
            1L
    )).thenReturn(true);

    RuntimeException excepcion =
            assertThrows(
                    RuntimeException.class,
                    () -> jornadaService.abrirJornada(
                            1L,
                            1L,
                            "Intento duplicado"
                    )
            );

    assertEquals(
            "La jornada ya fue creada para este turno",
            excepcion.getMessage()
    );

    verify(jornadaRepository)
            .existsByFechaAndTurno_Id(
                    LocalDate.now(),
                    1L
            );

    verify(usuarioRepository, never())
            .findById(anyLong());

    verify(turnoRepository, never())
            .findById(anyLong());

    verify(jornadaRepository, never())
            .save(any(Jornada.class));

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
void debeRechazarAperturaCuandoUsuarioNoExiste() {

    when(jornadaRepository.existsByFechaAndTurno_Id(
            LocalDate.now(),
            1L
    )).thenReturn(false);

    when(usuarioRepository.findById(999L))
            .thenReturn(Optional.empty());

    RuntimeException excepcion =
            assertThrows(
                    RuntimeException.class,
                    () -> jornadaService.abrirJornada(
                            999L,
                            1L,
                            "Jornada"
                    )
            );

    assertEquals(
            "Usuario no encontrado",
            excepcion.getMessage()
    );

    verify(usuarioRepository)
            .findById(999L);

    verify(turnoRepository, never())
            .findById(anyLong());

    verify(jornadaRepository, never())
            .save(any(Jornada.class));
}
@Test
void debeRechazarAperturaCuandoTurnoNoExiste() {

    when(jornadaRepository.existsByFechaAndTurno_Id(
            LocalDate.now(),
            999L
    )).thenReturn(false);

    when(usuarioRepository.findById(1L))
            .thenReturn(Optional.of(usuarioValido));

    when(turnoRepository.findById(999L))
            .thenReturn(Optional.empty());

    RuntimeException excepcion =
            assertThrows(
                    RuntimeException.class,
                    () -> jornadaService.abrirJornada(
                            1L,
                            999L,
                            "Jornada"
                    )
            );

    assertEquals(
            "Turno no encontrado",
            excepcion.getMessage()
    );

    verify(usuarioRepository)
            .findById(1L);

    verify(turnoRepository)
            .findById(999L);

    verify(jornadaRepository, never())
            .save(any(Jornada.class));
}
@Test
void debeIndicarQueLaJornadaEstaAbierta() {

    Jornada jornada = new Jornada();

    jornada.setFecha(LocalDate.now());
    jornada.setTurno(turnoValido);
    jornada.setHabilitada(true);
    jornada.setCerrada(false);

    when(jornadaRepository.findByFechaAndTurno_Id(
            LocalDate.now(),
            1L
    )).thenReturn(Optional.of(jornada));

    boolean resultado =
            jornadaService.jornadaAbierta(
                    LocalDate.now(),
                    1L
            );

    assertTrue(resultado);

    verify(jornadaRepository)
            .findByFechaAndTurno_Id(
                    LocalDate.now(),
                    1L
            );
}
@Test
void debeRechazarCierreCuandoJornadaNoExiste() {

    when(jornadaRepository.findByFechaAndTurno_Id(
            LocalDate.now(),
            1L
    )).thenReturn(Optional.empty());

    RuntimeException excepcion =
            assertThrows(
                    RuntimeException.class,
                    () -> jornadaService.cerrarJornada(
                            1L,
                            1L,
                            "Cierre",
                            false
                    )
            );

    assertEquals(
            "No existe jornada abierta",
            excepcion.getMessage()
    );

    verify(jornadaRepository)
            .findByFechaAndTurno_Id(
                    LocalDate.now(),
                    1L
            );

    verify(turnoRepository, never())
            .findById(anyLong());

    verify(usuarioRepository, never())
            .findById(anyLong());

    verify(asistenciaService, never())
            .procesarCierreJornada(anyLong());

    verify(jornadaRepository, never())
            .save(any(Jornada.class));
}
@Test
void debeRechazarCierreCuandoJornadaYaEstaCerrada() {

    Jornada jornada = new Jornada();

    jornada.setFecha(LocalDate.now());
    jornada.setTurno(turnoValido);
    jornada.setHabilitada(false);
    jornada.setCerrada(true);

    when(jornadaRepository.findByFechaAndTurno_Id(
            LocalDate.now(),
            1L
    )).thenReturn(Optional.of(jornada));

    RuntimeException excepcion =
            assertThrows(
                    RuntimeException.class,
                    () -> jornadaService.cerrarJornada(
                            1L,
                            1L,
                            "Segundo cierre",
                            false
                    )
            );

    assertEquals(
            "La jornada ya fue cerrada",
            excepcion.getMessage()
    );

    verify(turnoRepository, never())
            .findById(anyLong());

    verify(asistenciaService, never())
            .procesarCierreJornada(anyLong());

    verify(jornadaRepository, never())
            .save(any(Jornada.class));
}
@Test
void debeRechazarCierreAnticipadoSinForzar() {

    Jornada jornada = new Jornada();

    jornada.setFecha(LocalDate.now());
    jornada.setTurno(turnoValido);
    jornada.setHabilitada(true);
    jornada.setCerrada(false);

    Turno turno = new Turno();
    turno.setId(1L);
    turno.setNombre("Turno Test");
    turno.setHoraInicio(LocalTime.of(0, 0));
    turno.setHoraFin(LocalTime.of(23, 59));
    turno.setActivo(true);

    when(jornadaRepository.findByFechaAndTurno_Id(
            LocalDate.now(),
            1L
    )).thenReturn(Optional.of(jornada));

    when(turnoRepository.findById(1L))
            .thenReturn(Optional.of(turno));

    RuntimeException excepcion =
            assertThrows(
                    RuntimeException.class,
                    () -> jornadaService.cerrarJornada(
                            1L,
                            1L,
                            "Cierre anticipado",
                            false
                    )
            );

    assertEquals(
            "CIERRE_ANTICIPADO",
            excepcion.getMessage()
    );

    verify(asistenciaService, never())
            .procesarCierreJornada(anyLong());

    verify(jornadaRepository, never())
            .save(any(Jornada.class));
}
@Test
void debePermitirCierreAnticipadoCuandoSeFuerza() {

    Jornada jornada = new Jornada();

    jornada.setFecha(LocalDate.now());
    jornada.setTurno(turnoValido);
    jornada.setHabilitada(true);
    jornada.setCerrada(false);

    when(jornadaRepository.findByFechaAndTurno_Id(
            LocalDate.now(),
            1L
    )).thenReturn(Optional.of(jornada));

    when(turnoRepository.findById(1L))
            .thenReturn(Optional.of(turnoValido));

    when(usuarioRepository.findById(1L))
            .thenReturn(Optional.of(usuarioValido));

    when(jornadaRepository.save(any(Jornada.class)))
            .thenAnswer(invocation ->
                    invocation.getArgument(0)
            );

    jornadaService.cerrarJornada(
            1L,
            1L,
            "Cierre forzado",
            true
    );

    assertTrue(
            jornada.getCerrada()
    );

    assertFalse(
            jornada.getHabilitada()
    );

    assertEquals(
            usuarioValido,
            jornada.getUsuarioCierre()
    );

    assertEquals(
            "Cierre forzado",
            jornada.getObservaciones()
    );

    assertFalse(
            jornada.getCierreAutomatico()
    );

    assertNotNull(
            jornada.getFechaCierre()
    );

    verify(asistenciaService)
            .procesarCierreJornada(1L);

    verify(jornadaRepository)
            .save(jornada);

    verify(auditoriaRespaldoService)
            .registrarEnTransaccion(
                    eq("usuarioTest"),
                    eq("JORNADAS"),
                    eq("CIERRE"),
                    eq("EXITOSO"),
                    isNull(),
                    contains("Se cerró la jornada")
            );
}
@Test
void debeRechazarCierreCuandoTurnoNoExiste() {

    Jornada jornada = new Jornada();

    jornada.setFecha(LocalDate.now());
    jornada.setTurno(turnoValido);
    jornada.setHabilitada(true);
    jornada.setCerrada(false);

    when(jornadaRepository.findByFechaAndTurno_Id(
            LocalDate.now(),
            1L
    )).thenReturn(Optional.of(jornada));

    when(turnoRepository.findById(1L))
            .thenReturn(Optional.empty());

    RuntimeException excepcion =
            assertThrows(
                    RuntimeException.class,
                    () -> jornadaService.cerrarJornada(
                            1L,
                            1L,
                            "Cierre",
                            true
                    )
            );

    assertEquals(
            "Turno no encontrado",
            excepcion.getMessage()
    );

    verify(usuarioRepository, never())
            .findById(anyLong());

    verify(asistenciaService, never())
            .procesarCierreJornada(anyLong());

    verify(jornadaRepository, never())
            .save(any(Jornada.class));
}
@Test
void debeRechazarCierreCuandoUsuarioNoExiste() {

    Jornada jornada = new Jornada();

    jornada.setFecha(LocalDate.now());
    jornada.setTurno(turnoValido);
    jornada.setHabilitada(true);
    jornada.setCerrada(false);

    when(jornadaRepository.findByFechaAndTurno_Id(
            LocalDate.now(),
            1L
    )).thenReturn(Optional.of(jornada));

    when(turnoRepository.findById(1L))
            .thenReturn(Optional.of(turnoValido));

    when(usuarioRepository.findById(999L))
            .thenReturn(Optional.empty());

    RuntimeException excepcion =
            assertThrows(
                    RuntimeException.class,
                    () -> jornadaService.cerrarJornada(
                            999L,
                            1L,
                            "Cierre",
                            true
                    )
            );

    assertEquals(
            "Usuario no encontrado",
            excepcion.getMessage()
    );

    verify(usuarioRepository)
            .findById(999L);

    verify(asistenciaService, never())
            .procesarCierreJornada(anyLong());

    verify(jornadaRepository, never())
            .save(any(Jornada.class));
}
@Test
void debeProcesarAsistenciasAlCerrarJornada() {

    Jornada jornada = new Jornada();

    jornada.setFecha(LocalDate.now());
    jornada.setTurno(turnoValido);
    jornada.setHabilitada(true);
    jornada.setCerrada(false);

    when(jornadaRepository.findByFechaAndTurno_Id(
            LocalDate.now(),
            1L
    )).thenReturn(Optional.of(jornada));

    when(turnoRepository.findById(1L))
            .thenReturn(Optional.of(turnoValido));

    when(usuarioRepository.findById(1L))
            .thenReturn(Optional.of(usuarioValido));

    when(jornadaRepository.save(any(Jornada.class)))
            .thenAnswer(invocation ->
                    invocation.getArgument(0)
            );

    jornadaService.cerrarJornada(
            1L,
            1L,
            "Cierre",
            true
    );

    verify(asistenciaService)
            .procesarCierreJornada(1L);

    verify(jornadaRepository)
            .save(jornada);
}
@Test
void debeBuscarJornadaPorFechaYTurno() {

    LocalDate fecha = LocalDate.now();

    Jornada jornada = new Jornada();

    jornada.setFecha(fecha);
    jornada.setTurno(turnoValido);

    when(jornadaRepository.findByFechaAndTurno_Id(
            fecha,
            1L
    )).thenReturn(Optional.of(jornada));

    Jornada resultado =
            jornadaService.buscarPorFechaYTurno(
                    fecha,
                    1L
            );

    assertNotNull(resultado);

    assertEquals(
            jornada,
            resultado
    );

    assertEquals(
            fecha,
            resultado.getFecha()
    );

    assertEquals(
            turnoValido,
            resultado.getTurno()
    );

    verify(jornadaRepository)
            .findByFechaAndTurno_Id(
                    fecha,
                    1L
            );
}
@Test
void debeRechazarBusquedaCuandoJornadaNoExiste() {

    LocalDate fecha = LocalDate.now();

    when(jornadaRepository.findByFechaAndTurno_Id(
            fecha,
            999L
    )).thenReturn(Optional.empty());

    RuntimeException excepcion =
            assertThrows(
                    RuntimeException.class,
                    () -> jornadaService.buscarPorFechaYTurno(
                            fecha,
                            999L
                    )
            );

    assertEquals(
            "No existe jornada",
            excepcion.getMessage()
    );

    verify(jornadaRepository)
            .findByFechaAndTurno_Id(
                    fecha,
                    999L
            );
}
@Test
void debeIndicarFalseCuandoNoExisteJornada() {

    LocalDate fecha = LocalDate.now();

    when(jornadaRepository.findByFechaAndTurno_Id(
            fecha,
            1L
    )).thenReturn(Optional.empty());

    boolean resultado =
            jornadaService.jornadaAbierta(
                    fecha,
                    1L
            );

    assertFalse(resultado);

    verify(jornadaRepository)
            .findByFechaAndTurno_Id(
                    fecha,
                    1L
            );
}
@Test
void debeCerrarAutomaticamenteJornadaDeDiaAnterior() {

    Jornada jornada = new Jornada();
    jornada.setId(10L);
    jornada.setFecha(LocalDate.now().minusDays(1));
    jornada.setTurno(turnoValido);
    jornada.setHabilitada(true);
    jornada.setCerrada(false);
    jornada.setCierreAutomatico(false);

    when(jornadaRepository.findByHabilitadaTrueAndCerradaFalse())
            .thenReturn(List.of(jornada));

    jornadaService.cerrarJornadasPendientes();
    verify(cierreJornadaIndividualService, times(1))
        .cerrar(10L);
    
    verify(jornadaRepository)
            .findByHabilitadaTrueAndCerradaFalse();

}
    
@Test
void noDebeCerrarAutomaticamenteJornadaDelDiaActual() {

    Jornada jornada = new Jornada();

    jornada.setFecha(LocalDate.now());
    jornada.setTurno(turnoValido);
    jornada.setHabilitada(true);
    jornada.setCerrada(false);
    jornada.setCierreAutomatico(false);

    when(jornadaRepository.findByHabilitadaTrueAndCerradaFalse())
            .thenReturn(List.of(jornada));

    jornadaService.cerrarJornadasPendientes();

    assertFalse(jornada.getCerrada());

    assertTrue(jornada.getHabilitada());

    assertFalse(jornada.getCierreAutomatico());

    verify(jornadaRepository, never())
            .save(any(Jornada.class));

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
void noDebeCerrarNadaCuandoNoHayJornadasPendientes() {

    when(jornadaRepository.findByHabilitadaTrueAndCerradaFalse())
            .thenReturn(List.of());

    jornadaService.cerrarJornadasPendientes();

    verify(jornadaRepository)
            .findByHabilitadaTrueAndCerradaFalse();

    verify(jornadaRepository, never())
            .save(any(Jornada.class));

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
void debeCerrarAutomaticamenteVariasJornadasPendientes() {

    Jornada jornada1 = new Jornada();
    
    jornada1.setId(10L);
    jornada1.setFecha(LocalDate.now().minusDays(1));
    jornada1.setTurno(turnoValido);
    jornada1.setHabilitada(true);
    jornada1.setCerrada(false);
    jornada1.setCierreAutomatico(false);


    Turno segundoTurno = new Turno();

    segundoTurno.setId(2L);
    segundoTurno.setNombre("Turno Tarde");
    segundoTurno.setHoraInicio(LocalTime.of(13, 0));
    segundoTurno.setHoraFin(LocalTime.of(18, 0));
    segundoTurno.setActivo(true);


    Jornada jornada2 = new Jornada();

    jornada2.setId(20L);
    jornada2.setFecha(LocalDate.now().minusDays(2));
    jornada2.setTurno(segundoTurno);
    jornada2.setHabilitada(true);
    jornada2.setCerrada(false);
    jornada2.setCierreAutomatico(false);


    when(jornadaRepository.findByHabilitadaTrueAndCerradaFalse())
            .thenReturn(List.of(jornada1, jornada2));

    jornadaService.cerrarJornadasPendientes();
    
    verify(cierreJornadaIndividualService, times(1))
        .cerrar(10L);

    verify(cierreJornadaIndividualService, times(1))
        .cerrar(20L);



    
}
@Test
void debeListarTodasLasJornadas() {

    Jornada jornada1 = new Jornada();
    jornada1.setFecha(LocalDate.now());

    Jornada jornada2 = new Jornada();
    jornada2.setFecha(LocalDate.now().minusDays(1));

    when(jornadaRepository.findAll())
            .thenReturn(List.of(jornada1, jornada2));

    List<Jornada> resultado =
            jornadaService.listar();

    assertNotNull(resultado);

    assertEquals(2, resultado.size());

    assertEquals(
            jornada1,
            resultado.get(0)
    );

    assertEquals(
            jornada2,
            resultado.get(1)
    );

    verify(jornadaRepository)
            .findAll();
}
@Test
void debeListarJornadasPorTurno() {

    Jornada jornada1 = new Jornada();
    jornada1.setTurno(turnoValido);

    Jornada jornada2 = new Jornada();
    jornada2.setTurno(turnoValido);

    when(jornadaRepository.findByTurno_Id(1L))
            .thenReturn(List.of(jornada1, jornada2));

    List<Jornada> resultado =
            jornadaService.listarPorTurno(1L);

    assertNotNull(resultado);
    assertEquals(2, resultado.size());

    assertEquals(jornada1, resultado.get(0));
    assertEquals(jornada2, resultado.get(1));

    verify(jornadaRepository)
            .findByTurno_Id(1L);
}
@Test
void debeDevolverListaVaciaCuandoTurnoNoTieneJornadas() {

    when(jornadaRepository.findByTurno_Id(999L))
            .thenReturn(List.of());

    List<Jornada> resultado =
            jornadaService.listarPorTurno(999L);

    assertNotNull(resultado);
    assertTrue(resultado.isEmpty());

    verify(jornadaRepository)
            .findByTurno_Id(999L);
}
@Test
void debeListarJornadasAbiertas() {

    Jornada jornada1 = new Jornada();
    jornada1.setHabilitada(true);
    jornada1.setCerrada(false);

    Jornada jornada2 = new Jornada();
    jornada2.setHabilitada(true);
    jornada2.setCerrada(false);

    when(jornadaRepository.findByHabilitadaTrueAndCerradaFalse())
            .thenReturn(List.of(jornada1, jornada2));

    List<Jornada> resultado =
            jornadaService.listarAbiertas();

    assertNotNull(resultado);
    assertEquals(2, resultado.size());

    assertEquals(jornada1, resultado.get(0));
    assertEquals(jornada2, resultado.get(1));

    verify(jornadaRepository)
            .findByHabilitadaTrueAndCerradaFalse();
}
@Test
void debeIndicarFalseCuandoJornadaEstaCerrada() {

    Jornada jornada = new Jornada();

    jornada.setFecha(LocalDate.now());
    jornada.setTurno(turnoValido);
    jornada.setHabilitada(false);
    jornada.setCerrada(true);

    when(jornadaRepository.findByFechaAndTurno_Id(
            LocalDate.now(),
            1L
    )).thenReturn(Optional.of(jornada));

    boolean resultado =
            jornadaService.jornadaAbierta(
                    LocalDate.now(),
                    1L
            );

    assertFalse(resultado);

    verify(jornadaRepository)
            .findByFechaAndTurno_Id(
                    LocalDate.now(),
                    1L
            );
}
@Test
void noDebeCerrarAutomaticamenteJornadaFutura() {

    Jornada jornada = new Jornada();

    jornada.setFecha(LocalDate.now().plusDays(1));
    jornada.setTurno(turnoValido);
    jornada.setHabilitada(true);
    jornada.setCerrada(false);
    jornada.setCierreAutomatico(false);

    when(jornadaRepository.findByHabilitadaTrueAndCerradaFalse())
            .thenReturn(List.of(jornada));

    jornadaService.cerrarJornadasPendientes();

    assertFalse(jornada.getCerrada());
    assertTrue(jornada.getHabilitada());
    assertFalse(jornada.getCierreAutomatico());

    verify(jornadaRepository, never())
            .save(any(Jornada.class));

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
void debeContinuarConOtrasJornadasSiUnaFalla() {

    Jornada jornada1 = new Jornada();
    jornada1.setId(10L);
    jornada1.setFecha(LocalDate.now().minusDays(1));
    jornada1.setTurno(turnoValido);
    jornada1.setHabilitada(true);
    jornada1.setCerrada(false);

    Jornada jornada2 = new Jornada();
    jornada2.setId(20L);
    jornada2.setFecha(LocalDate.now().minusDays(2));
    jornada2.setTurno(turnoValido);
    jornada2.setHabilitada(true);
    jornada2.setCerrada(false);

    when(jornadaRepository.findByHabilitadaTrueAndCerradaFalse())
            .thenReturn(List.of(jornada1, jornada2));

    // Simulamos que falla el cierre de la primera jornada.
    doThrow(new RuntimeException("Error simulado"))
            .when(cierreJornadaIndividualService)
            .cerrar(10L);

    // El error no debe interrumpir el procesamiento general.
    assertDoesNotThrow(() ->
            jornadaService.cerrarJornadasPendientes()
    );

    // Se intentó cerrar la primera jornada.
    verify(cierreJornadaIndividualService, times(1))
            .cerrar(10L);

    // A pesar del error, se intentó cerrar la segunda.
    verify(cierreJornadaIndividualService, times(1))
            .cerrar(20L);

    // JornadaService no debe guardar jornadas directamente.
    verify(jornadaRepository, never())
            .save(any(Jornada.class));
}
}