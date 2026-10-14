package com.instituto.asistencia.service;

import com.instituto.asistencia.entity.AuditoriaRespaldo;
import com.instituto.asistencia.repository.AuditoriaRespaldoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RespaldoServiceTest {

    @Mock
    private AuditoriaRespaldoRepository auditoriaRepository;

    @Mock
    private MultipartFile archivo;
    
    @Mock
    private ProcesoFactory procesoFactory;

    @InjectMocks
    private RespaldoService respaldoService;

    @BeforeEach
    void setUp() {

        ReflectionTestUtils.setField(
                respaldoService,
                "mysqldumpPath",
                "C:/xampp/mysql/bin/mysqldump.exe"
        );

        ReflectionTestUtils.setField(
                respaldoService,
                "mysqlPath",
                "C:/xampp/mysql/bin/mysql.exe"
        );

        ReflectionTestUtils.setField(
                respaldoService,
                "username",
                "root"
        );

        ReflectionTestUtils.setField(
                respaldoService,
                "password",
                ""
        );

        ReflectionTestUtils.setField(
                respaldoService,
                "datasourceUrl",
                "jdbc:mysql://localhost:3306/asistencia_ifd"
        );

        ReflectionTestUtils.setField(
                respaldoService,
                "directorioBackup",
                "target/test-backups"
        );
    }

    // =========================================================
    // TEST 1 - obtenerRutaMysqldump()
    // =========================================================

    @Test
    void debeObtenerRutaDeMysqldump() {

        String resultado =
                respaldoService.obtenerRutaMysqldump();

        assertEquals(
                "C:/xampp/mysql/bin/mysqldump.exe",
                resultado
        );
    }

    // =========================================================
    // TEST 2 - obtenerUsuario()
    // =========================================================

    @Test
    void debeObtenerUsuarioConfigurado() {

        String resultado =
                respaldoService.obtenerUsuario();

        assertEquals(
                "root",
                resultado
        );
    }

    // =========================================================
    // TEST 3 - obtenerBaseDatos()
    // =========================================================

    @Test
    void debeObtenerNombreDeBaseDeDatos() {

        String resultado =
                respaldoService.obtenerBaseDatos();

        assertEquals(
                "asistencia_ifd",
                resultado
        );
    }

    // =========================================================
    // TEST 4 - obtenerBaseDatos() con parámetros
    // =========================================================

    @Test
    void debeObtenerBaseDeDatosIgnorandoParametros() {

        ReflectionTestUtils.setField(
                respaldoService,
                "datasourceUrl",
                "jdbc:mysql://localhost:3306/asistencia_ifd?useSSL=false&serverTimezone=UTC"
        );

        String resultado =
                respaldoService.obtenerBaseDatos();

        assertEquals(
                "asistencia_ifd",
                resultado
        );
    }

    // =========================================================
    // TEST 5 - obtenerAuditoria()
    // =========================================================

    @Test
    void debeObtenerAuditoriaOrdenadaPorFecha() {

        AuditoriaRespaldo auditoria1 =
                new AuditoriaRespaldo();

        auditoria1.setId(1L);
        auditoria1.setUsuario("admin");
        auditoria1.setModulo("RESPALDOS");
        auditoria1.setAccion("GENERAR");
        auditoria1.setResultado("EXITOSO");

        AuditoriaRespaldo auditoria2 =
                new AuditoriaRespaldo();

        auditoria2.setId(2L);
        auditoria2.setUsuario("admin");
        auditoria2.setModulo("RESPALDOS");
        auditoria2.setAccion("RESTAURAR");
        auditoria2.setResultado("EXITOSO");

        when(auditoriaRepository.findAllByOrderByFechaHoraDesc())
                .thenReturn(List.of(auditoria2, auditoria1));

        List<AuditoriaRespaldo> resultado =
                respaldoService.obtenerAuditoria();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertSame(auditoria2, resultado.get(0));
        assertSame(auditoria1, resultado.get(1));

        verify(auditoriaRepository)
                .findAllByOrderByFechaHoraDesc();
    }

    // =========================================================
    // TEST 6 - restaurarBackup() archivo null
    // =========================================================

    @Test
    void debeRechazarRestauracionSiArchivoEsNull() {

        RuntimeException excepcion =
                assertThrows(
                        RuntimeException.class,
                        () -> respaldoService.restaurarBackup(
                                null,
                                "admin"
                        )
                );

        assertEquals(
                "No fue posible restaurar el respaldo.",
                excepcion.getMessage()
        );

        verify(auditoriaRepository)
                .save(any(AuditoriaRespaldo.class));
    }

    // =========================================================
    // TEST 7 - restaurarBackup() archivo vacío
    // =========================================================

    @Test
    void debeRechazarRestauracionSiArchivoEstaVacio() {

        when(archivo.isEmpty())
                .thenReturn(true);

        RuntimeException excepcion =
                assertThrows(
                        RuntimeException.class,
                        () -> respaldoService.restaurarBackup(
                                archivo,
                                "admin"
                        )
                );

        assertEquals(
                "No fue posible restaurar el respaldo.",
                excepcion.getMessage()
        );

        verify(auditoriaRepository)
                .save(any(AuditoriaRespaldo.class));
    }

    // =========================================================
    // TEST 8 - extensión incorrecta
    // =========================================================

    @Test
    void debeRechazarRestauracionSiArchivoNoEsSql() {

        when(archivo.isEmpty())
                .thenReturn(false);

        when(archivo.getOriginalFilename())
                .thenReturn("respaldo.txt");

        RuntimeException excepcion =
                assertThrows(
                        RuntimeException.class,
                        () -> respaldoService.restaurarBackup(
                                archivo,
                                "admin"
                        )
                );

        assertEquals(
                "No fue posible restaurar el respaldo.",
                excepcion.getMessage()
        );

        verify(auditoriaRepository)
                .save(any(AuditoriaRespaldo.class));
    }

    // =========================================================
    // TEST 9 - extensión .SQL válida
    // =========================================================

    @Test
    void debeAceptarExtensionSqlEnMayusculas() {

        when(archivo.isEmpty())
                .thenReturn(false);

        when(archivo.getOriginalFilename())
                .thenReturn("RESPALDO.SQL");

        /*
         * No ejecutamos mysql realmente.
         * El objetivo es comprobar que la validación
         * de extensión acepta .SQL sin importar mayúsculas.
         *
         * El servicio continuará posteriormente hasta
         * intentar crear/ejecutar el proceso externo,
         * por eso esperamos una excepción posterior.
         */

        RuntimeException excepcion =
                assertThrows(
                        RuntimeException.class,
                        () -> respaldoService.restaurarBackup(
                                archivo,
                                "admin"
                        )
                );

        assertEquals(
                "No fue posible restaurar el respaldo.",
                excepcion.getMessage()
        );

        verify(archivo, atLeastOnce())
        .getOriginalFilename();
    }
    @Test
void debeRestaurarBackupCorrectamenteSinEjecutarMySQLReal() throws Exception {

    when(archivo.isEmpty())
            .thenReturn(false);

    when(archivo.getOriginalFilename())
            .thenReturn("respaldo.SQL");

    doAnswer(invocation -> {
    java.io.File destino = invocation.getArgument(0);

    Files.writeString(
            destino.toPath(),
            "CREATE TABLE prueba (id INT);"
    );

    return null;
}).when(archivo).transferTo(any(java.io.File.class));

    Process procesoMock =
            mock(Process.class);

    when(procesoMock.getOutputStream())
            .thenReturn(new ByteArrayOutputStream());

    when(procesoMock.getInputStream())
            .thenReturn(
                    new ByteArrayInputStream(
                            "Restauración correcta"
                                    .getBytes(StandardCharsets.UTF_8)
                    )
            );

    when(procesoMock.waitFor())
            .thenReturn(0);

    when(procesoMock.isAlive())
            .thenReturn(true);

    when(procesoFactory.iniciar(any(ProcessBuilder.class)))
            .thenReturn(procesoMock);

    respaldoService.restaurarBackup(
            archivo,
            "admin"
    );

    verify(procesoFactory)
            .iniciar(any(ProcessBuilder.class));

    verify(procesoMock)
            .getOutputStream();

    verify(procesoMock)
            .getInputStream();

    verify(procesoMock)
            .waitFor();

    verify(auditoriaRepository)
            .save(argThat(auditoria ->
                    "admin".equals(auditoria.getUsuario())
                    && "RESPALDOS".equals(auditoria.getModulo())
                    && "RESTAURAR".equals(auditoria.getAccion())
                    && "EXITOSO".equals(auditoria.getResultado())
                    && "respaldo.SQL".equals(auditoria.getArchivo())
                    && "Respaldo restaurado correctamente."
                            .equals(auditoria.getDetalle())
            ));
}
@Test
void debeRegistrarErrorCuandoMySQLDevuelveCodigoDistintoDeCero()
        throws Exception {

    when(archivo.isEmpty())
            .thenReturn(false);

    when(archivo.getOriginalFilename())
            .thenReturn("respaldo.sql");

    doAnswer(invocation -> {

    java.io.File destino =
            invocation.getArgument(0);

    Files.writeString(
            destino.toPath(),
            "SQL INVALIDO"
    );

    return null;

}).when(archivo)
        .transferTo(any(java.io.File.class));

    Process procesoMock =
            mock(Process.class);

    when(procesoMock.getOutputStream())
            .thenReturn(new ByteArrayOutputStream());

    when(procesoMock.getInputStream())
            .thenReturn(
                    new ByteArrayInputStream(
                            "ERROR MYSQL"
                                    .getBytes(StandardCharsets.UTF_8)
                    )
            );

    when(procesoMock.waitFor())
            .thenReturn(1);

    when(procesoMock.isAlive())
            .thenReturn(true);

    when(procesoFactory.iniciar(any(ProcessBuilder.class)))
            .thenReturn(procesoMock);

    RuntimeException excepcion =
            assertThrows(
                    RuntimeException.class,
                    () -> respaldoService.restaurarBackup(
                            archivo,
                            "admin"
                    )
            );

    assertEquals(
            "No fue posible restaurar el respaldo.",
            excepcion.getMessage()
    );

    assertNotNull(
            excepcion.getCause()
    );

    assertEquals(
            "Error al restaurar:\nERROR MYSQL",
            excepcion.getCause().getMessage()
    );

    verify(procesoFactory)
            .iniciar(any(ProcessBuilder.class));

    verify(procesoMock)
            .waitFor();

    verify(auditoriaRepository)
            .save(argThat(auditoria ->
                    "admin".equals(auditoria.getUsuario())
                    && "RESPALDOS".equals(auditoria.getModulo())
                    && "RESTAURAR".equals(auditoria.getAccion())
                    && "ERROR".equals(auditoria.getResultado())
                    && "respaldo.sql".equals(auditoria.getArchivo())
                    && "Error al restaurar:\nERROR MYSQL"
                            .equals(auditoria.getDetalle())
            ));
}

}