package com.instituto.asistencia.service;

import com.instituto.asistencia.entity.AuditoriaRespaldo;
import com.instituto.asistencia.repository.AuditoriaRespaldoRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditoriaRespaldoServiceTest {

    @Mock
    private AuditoriaRespaldoRepository auditoriaRepository;

    @InjectMocks
    private AuditoriaRespaldoService auditoriaRespaldoService;


    // =====================================================
    // TESTS PARA registrar()
    // =====================================================

    @Test
    void debeRegistrarAuditoriaCorrectamente() {

        AuditoriaRespaldo auditoriaGuardada =
                new AuditoriaRespaldo();

        auditoriaGuardada.setUsuario("admin");
        auditoriaGuardada.setModulo("DOCENTES");
        auditoriaGuardada.setAccion("ALTA");
        auditoriaGuardada.setResultado("EXITOSO");
        auditoriaGuardada.setArchivo(null);
        auditoriaGuardada.setDetalle(
                "Se registró un nuevo docente"
        );

        when(auditoriaRepository.save(any(AuditoriaRespaldo.class)))
                .thenReturn(auditoriaGuardada);

        AuditoriaRespaldo resultado =
                auditoriaRespaldoService.registrar(
                        "admin",
                        "DOCENTES",
                        "ALTA",
                        "EXITOSO",
                        null,
                        "Se registró un nuevo docente"
                );

        assertNotNull(resultado);

        assertEquals(
                "admin",
                resultado.getUsuario()
        );

        assertEquals(
                "DOCENTES",
                resultado.getModulo()
        );

        assertEquals(
                "ALTA",
                resultado.getAccion()
        );

        assertEquals(
                "EXITOSO",
                resultado.getResultado()
        );

        assertNull(
                resultado.getArchivo()
        );

        assertEquals(
                "Se registró un nuevo docente",
                resultado.getDetalle()
        );

        verify(auditoriaRepository)
                .save(any(AuditoriaRespaldo.class));
    }


    @Test
    void debeRegistrarFechaHoraAutomaticamente() {

        when(auditoriaRepository.save(any(AuditoriaRespaldo.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        LocalDateTime antes =
                LocalDateTime.now();

        AuditoriaRespaldo resultado =
                auditoriaRespaldoService.registrar(
                        "admin",
                        "DOCENTES",
                        "ALTA",
                        "EXITOSO",
                        null,
                        "Prueba"
                );

        LocalDateTime despues =
                LocalDateTime.now();

        assertNotNull(
                resultado.getFechaHora()
        );

        assertFalse(
                resultado.getFechaHora().isBefore(antes)
        );

        assertFalse(
                resultado.getFechaHora().isAfter(despues)
        );
    }


    @Test
    void debeEnviarTodosLosDatosCorrectamenteAlRepositorio() {

        when(auditoriaRepository.save(any(AuditoriaRespaldo.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        AuditoriaRespaldo resultado =
                auditoriaRespaldoService.registrar(
                        "secretario",
                        "RESPALDOS",
                        "RESTAURACION",
                        "ERROR",
                        "respaldo.sql",
                        "Error al restaurar respaldo"
                );

        assertEquals(
                "secretario",
                resultado.getUsuario()
        );

        assertEquals(
                "RESPALDOS",
                resultado.getModulo()
        );

        assertEquals(
                "RESTAURACION",
                resultado.getAccion()
        );

        assertEquals(
                "ERROR",
                resultado.getResultado()
        );

        assertEquals(
                "respaldo.sql",
                resultado.getArchivo()
        );

        assertEquals(
                "Error al restaurar respaldo",
                resultado.getDetalle()
        );

        verify(auditoriaRepository)
                .save(any(AuditoriaRespaldo.class));
    }


    // =====================================================
    // TESTS PARA listar()
    // =====================================================

    @Test
    void debeListarAuditoriasOrdenadasPorFechaDescendente() {

        AuditoriaRespaldo auditoria1 =
                new AuditoriaRespaldo();

        AuditoriaRespaldo auditoria2 =
                new AuditoriaRespaldo();

        List<AuditoriaRespaldo> auditorias =
                Arrays.asList(
                        auditoria1,
                        auditoria2
                );

        when(auditoriaRepository
                .findAllByOrderByFechaHoraDesc())
                .thenReturn(auditorias);

        List<AuditoriaRespaldo> resultado =
                auditoriaRespaldoService.listar();

        assertNotNull(resultado);

        assertEquals(
                2,
                resultado.size()
        );

        assertSame(
                auditoria1,
                resultado.get(0)
        );

        assertSame(
                auditoria2,
                resultado.get(1)
        );

        verify(auditoriaRepository)
                .findAllByOrderByFechaHoraDesc();
    }


    // =====================================================
    // TESTS PARA listarPorModulo()
    // =====================================================

    @Test
    void debeListarTodasLasAuditoriasCuandoModuloEsRespaldos() {

        AuditoriaRespaldo auditoria1 =
                new AuditoriaRespaldo();

        AuditoriaRespaldo auditoria2 =
                new AuditoriaRespaldo();

        List<AuditoriaRespaldo> auditorias =
                Arrays.asList(
                        auditoria1,
                        auditoria2
                );

        when(auditoriaRepository
                .findAllByOrderByFechaHoraDesc())
                .thenReturn(auditorias);

        List<AuditoriaRespaldo> resultado =
                auditoriaRespaldoService
                        .listarPorModulo("RESPALDOS");

        assertNotNull(resultado);

        assertEquals(
                2,
                resultado.size()
        );

        assertSame(
                auditoria1,
                resultado.get(0)
        );

        assertSame(
                auditoria2,
                resultado.get(1)
        );

        verify(auditoriaRepository)
                .findAllByOrderByFechaHoraDesc();

        verify(auditoriaRepository, never())
                .findByModuloOrderByFechaHoraDesc(anyString());
    }


    @Test
    void debeListarAuditoriasPorModuloEspecifico() {

        AuditoriaRespaldo auditoria =
                new AuditoriaRespaldo();

        auditoria.setModulo("DOCENTES");

        List<AuditoriaRespaldo> auditorias =
                List.of(auditoria);

        when(auditoriaRepository
                .findByModuloOrderByFechaHoraDesc("DOCENTES"))
                .thenReturn(auditorias);

        List<AuditoriaRespaldo> resultado =
                auditoriaRespaldoService
                        .listarPorModulo("DOCENTES");

        assertNotNull(resultado);

        assertEquals(
                1,
                resultado.size()
        );

        assertSame(
                auditoria,
                resultado.get(0)
        );

        verify(auditoriaRepository)
                .findByModuloOrderByFechaHoraDesc("DOCENTES");

        verify(auditoriaRepository, never())
                .findAllByOrderByFechaHoraDesc();
    }
}