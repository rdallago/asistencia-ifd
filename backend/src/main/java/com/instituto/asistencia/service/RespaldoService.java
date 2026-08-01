package com.instituto.asistencia.service;

import com.instituto.asistencia.entity.AuditoriaRespaldo;
import com.instituto.asistencia.repository.AuditoriaRespaldoRepository;
import org.springframework.web.multipart.MultipartFile;
import java.util.stream.Collectors;
import java.io.OutputStream;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class RespaldoService {

    @Value("${backup.mysqldump:mysqldump}")
    private String mysqldumpPath;
    
    @Value("${backup.mysql.path:mysql}")
    private String mysqlPath;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${backup.directorio:backups}")
    private String directorioBackup;

    private final AuditoriaRespaldoRepository auditoriaRepository;
    
public RespaldoService(
            AuditoriaRespaldoRepository auditoriaRepository) {

        this.auditoriaRepository = auditoriaRepository;
    }

    public String obtenerRutaMysqldump() {

        return mysqldumpPath;
    }

    public String obtenerUsuario() {

        return username;
    }

    public String obtenerBaseDatos() {

        return extraerBaseDatos(datasourceUrl);
    }
private void registrarAuditoria(
        String usuario,
        String operacion,
        String resultado,
        String archivo,
        String detalle) {

    AuditoriaRespaldo auditoria =
            new AuditoriaRespaldo();

    auditoria.setUsuario(usuario);
    auditoria.setFechaHora(LocalDateTime.now());
    auditoria.setOperacion(operacion);
    auditoria.setResultado(resultado);
    auditoria.setArchivo(archivo);
    auditoria.setDetalle(detalle);

    auditoriaRepository.saveAndFlush(auditoria);
}
    public Path generarBackup(String usuario) {

    Path archivoBackup = null;

    try {

        String baseDatos = obtenerBaseDatos();

        Path directorio = Paths.get(directorioBackup);
        Files.createDirectories(directorio);

        String fechaHora = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        String nombreArchivo =
                "backup_" + baseDatos + "_" + fechaHora + ".sql";

        archivoBackup = directorio.resolve(nombreArchivo);

       ProcessBuilder processBuilder =
        new ProcessBuilder(
                mysqldumpPath,
                "-u",
                username,
                "--password=",

                baseDatos,

                "instituciones",
                "turnos",
                "docentes",
                "usuarios",
                "docente_turnos",
                "jornadas",
                "asistencias"
        );
       System.out.println("COMANDO RESTAURAR:");

for (String s : processBuilder.command()) {
    System.out.println("[" + s + "]");
}
       
        Process proceso = processBuilder.start();

        // Escribir directamente el SQL al archivo
        try (
                InputStream is = proceso.getInputStream();
                OutputStream os = Files.newOutputStream(archivoBackup)
        ) {

            is.transferTo(os);

        }

        // Leer errores (NO se mezclan con el SQL)
        String errores;

        try (
                BufferedReader err =
                        new BufferedReader(
                                new InputStreamReader(
                                        proceso.getErrorStream(),
                                        StandardCharsets.UTF_8
                                )
                        )
        ) {

            errores = err.lines()
                    .collect(Collectors.joining(System.lineSeparator()));

        }

        int codigo = proceso.waitFor();

        if (codigo != 0) {

            registrarAuditoria(
                    usuario,
                    "GENERAR",
                    "ERROR",
                    archivoBackup.toString(),
                    errores
            );

            Files.deleteIfExists(archivoBackup);

            throw new RuntimeException(
                    "Error al generar respaldo:\n" + errores
            );

        }

        registrarAuditoria(
                usuario,
                "GENERAR",
                "EXITOSO",
                archivoBackup.toString(),
                "Respaldo generado correctamente."
        );

        return archivoBackup;

    } catch (Exception e) {

        try {

            if (archivoBackup != null) {

                Files.deleteIfExists(archivoBackup);

            }

        } catch (Exception ignored) {
        }

        registrarAuditoria(
                usuario,
                "GENERAR",
                "ERROR",
                archivoBackup != null
                        ? archivoBackup.toString()
                        : null,
                e.getMessage()
        );

        throw new RuntimeException(
                "No fue posible generar el respaldo.",
                e
        );

    }

}
 public void restaurarBackup(
        MultipartFile archivo,
        String usuario) {

    Path archivoTemporal = null;

    try {

        // ==========================================
        // Validar archivo
        // ==========================================

        if (archivo == null || archivo.isEmpty()) {

            throw new RuntimeException(
                    "El archivo de respaldo está vacío."
            );

        }

        String nombre =
                archivo.getOriginalFilename();

        if (nombre == null ||
                !nombre.toLowerCase().endsWith(".sql")) {

            throw new RuntimeException(
                    "El archivo debe tener extensión .sql."
            );

        }

        // ==========================================
        // Crear archivo temporal
        // ==========================================

        Path directorioTemporal =
                Files.createTempDirectory(
                        "restauracion_backup_"
                );

        archivoTemporal =
                directorioTemporal.resolve(
                        "respaldo.sql"
                );

        archivo.transferTo(
                archivoTemporal.toFile()
        );


        // Obtener base de datos
        String baseDatos = obtenerBaseDatos();


        // ==========================================
        // Construir proceso MYSQL
        // ==========================================

        ProcessBuilder processBuilder;

        if (password == null || password.isBlank()) {

            processBuilder =
                    new ProcessBuilder(
                            mysqlPath,
                            "-u",
                            username,
                            "--password=",
                            baseDatos
                    );

        } else {

            processBuilder =
                    new ProcessBuilder(
                            mysqlPath,
                            "-u",
                            username,
                            "-p" + password,
                            baseDatos
                    );

        }


        processBuilder.redirectErrorStream(true);


        System.out.println("COMANDO RESTAURAR:");

        for (String s : processBuilder.command()) {
            System.out.println("[" + s + "]");
        }


        Process proceso =
                processBuilder.start();


        System.out.println(
                "MYSQL INICIADO: "
                + proceso.isAlive()
        );


        // ==========================================
        // Enviar archivo SQL a MYSQL
        // ==========================================

        try (
                OutputStream os =
                        proceso.getOutputStream();

                InputStream archivoSql =
                        Files.newInputStream(archivoTemporal)
        ) {

            archivoSql.transferTo(os);

            os.flush();

        }


        // ==========================================
        // Leer respuesta MYSQL
        // ==========================================

        String salida;

        try (
                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(
                                        proceso.getInputStream(),
                                        StandardCharsets.UTF_8
                                )
                        )
        ) {

            salida =
                    reader.lines()
                            .collect(
                                    Collectors.joining(
                                            System.lineSeparator()
                                    )
                            );

        }


        int codigo =
                proceso.waitFor();


        System.out.println("---------------------------");
        System.out.println("MYSQL: " + mysqlPath);
        System.out.println("USUARIO: " + username);
        System.out.println("BASE DATOS: " + baseDatos);
        System.out.println("ARCHIVO: " + archivoTemporal);
        System.out.println("CODIGO MYSQL: " + codigo);
        System.out.println("SALIDA MYSQL:");
        System.out.println(salida);
        System.out.println("---------------------------");


        if (codigo != 0) {

            throw new RuntimeException(
                    "Error al restaurar:\n" + salida
            );

        }


        registrarAuditoria(
                usuario,
                "RESTAURAR",
                "EXITOSO",
                nombre,
                "Respaldo restaurado correctamente."
        );


    } catch (Exception e) {


        registrarAuditoria(
                usuario,
                "RESTAURAR",
                "ERROR",
                archivo != null
                        ? archivo.getOriginalFilename()
                        : null,
                e.getMessage()
        );


        throw new RuntimeException(
                "No fue posible restaurar el respaldo.",
                e
        );


    } finally {


        if (archivoTemporal != null) {

            try {

                Files.deleteIfExists(
                        archivoTemporal
                );

                Files.deleteIfExists(
                        archivoTemporal.getParent()
                );

            } catch (Exception ignored) {

            }

        }

    }

}
  
    private String extraerBaseDatos(String url) {

        String[] partes = url.split("/");

        String ultimaParte =
                partes[partes.length - 1];

        int posicionParametros =
                ultimaParte.indexOf("?");

        if (posicionParametros >= 0) {

            ultimaParte =
                    ultimaParte.substring(
                            0,
                            posicionParametros
                    );
        }

        return ultimaParte;
    }
    
    public List<AuditoriaRespaldo> obtenerAuditoria() {

    return auditoriaRepository
            .findAllByOrderByFechaHoraDesc();

}
}