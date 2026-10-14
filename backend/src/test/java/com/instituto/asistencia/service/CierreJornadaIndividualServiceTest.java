
package com.instituto.asistencia.service;

import com.instituto.asistencia.entity.Jornada;
import com.instituto.asistencia.entity.Turno;
import com.instituto.asistencia.repository.JornadaRepository;
import java.time.LocalDate;
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
class CierreJornadaIndividualServiceTest {

    @Mock
    private JornadaRepository jornadaRepository;

    @Mock
    private AsistenciaService asistenciaService;

    @Mock
    private AuditoriaRespaldoService auditoriaRespaldoService;

    @InjectMocks
    private CierreJornadaIndividualService cierreService;

    private Jornada jornada;

    @BeforeEach
    void configurar() {

        Turno turno = new Turno();
        turno.setNombre("Turno Noche");

        jornada = new Jornada();
        jornada.setId(10L);
        jornada.setFecha(LocalDate.now().minusDays(1));
        jornada.setTurno(turno);
        jornada.setHabilitada(true);
        jornada.setCerrada(false);
        jornada.setCierreAutomatico(false);
    }

    @Test
    void debeCerrarCorrectamenteJornadaAnterior() {

        when(jornadaRepository.findById(10L))
                .thenReturn(Optional.of(jornada));

        when(jornadaRepository.save(jornada))
                .thenReturn(jornada);

        cierreService.cerrar(10L);

        verify(asistenciaService)
                .procesarCierreJornada(jornada);

        assertTrue(jornada.getCerrada());
        assertFalse(jornada.getHabilitada());
        assertTrue(jornada.getCierreAutomatico());
        assertNotNull(jornada.getFechaCierre());

        verify(jornadaRepository).save(jornada);

        verify(auditoriaRespaldoService)
                .registrarEnTransaccion(
                        eq("SISTEMA"),
                        eq("JORNADAS"),
                        eq("CIERRE_AUTOMATICO"),
                        eq("EXITOSO"),
                        isNull(),
                        contains("Se cerró automáticamente")
                );
    }

    @Test
    void noDebeCerrarJornadaYaCerrada() {

        jornada.setCerrada(true);

        when(jornadaRepository.findById(10L))
                .thenReturn(Optional.of(jornada));

        assertThrows(
                IllegalStateException.class,
                () -> cierreService.cerrar(10L)
        );

        verifyNoInteractions(asistenciaService);
        verify(jornadaRepository, never()).save(any());
        verifyNoInteractions(auditoriaRespaldoService);
    }

    @Test
    void noDebeCerrarJornadaDelDiaActual() {

        jornada.setFecha(LocalDate.now());

        when(jornadaRepository.findById(10L))
                .thenReturn(Optional.of(jornada));

        assertThrows(
                IllegalStateException.class,
                () -> cierreService.cerrar(10L)
        );

        verifyNoInteractions(asistenciaService);
        verify(jornadaRepository, never()).save(any());
        verifyNoInteractions(auditoriaRespaldoService);
    }

    @Test
    void noDebeCerrarSiFallaProcesamientoDeAsistencias() {

        when(jornadaRepository.findById(10L))
                .thenReturn(Optional.of(jornada));

        doThrow(new RuntimeException("Error simulado"))
                .when(asistenciaService)
                .procesarCierreJornada(jornada);

        assertThrows(
                RuntimeException.class,
                () -> cierreService.cerrar(10L)
        );

        assertFalse(jornada.getCerrada());
        assertTrue(jornada.getHabilitada());

        verify(jornadaRepository, never()).save(any());
        verifyNoInteractions(auditoriaRespaldoService);
    }

    @Test
    void noDebeCerrarJornadaInexistente() {

        when(jornadaRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> cierreService.cerrar(99L)
        );

        verifyNoInteractions(asistenciaService);
        verify(jornadaRepository, never()).save(any());
        verifyNoInteractions(auditoriaRespaldoService);
    }
}
