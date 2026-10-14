package com.instituto.asistencia;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class RollbackIntegracionTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void debeInsertarDocenteYRevertirCambios() throws Exception {

        // 1. Comprobar la base de datos
        try (var conexion = dataSource.getConnection()) {
            assertEquals(
                "asistencia_ifd_test",
                conexion.getCatalog(),
                "La prueba no está utilizando la base de test"
            );
        }

        // 2. Insertar docente ficticio
        String dniPrueba = "99999991";

        jdbcTemplate.update(
            """
            INSERT INTO docentes
                (dni, apellido, nombre, cargo, activo, fecha_alta)
            VALUES (?, ?, ?, ?, ?, CURDATE())
            """,
            dniPrueba,
            "PRUEBA",
            "DOCENTE TEMPORAL",
            "DOCENTE",
            true
        );

        // 3. Verificar que se insertó
        Integer cantidad = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM docentes WHERE dni = ?",
            Integer.class,
            dniPrueba
        );

        assertEquals(1, cantidad);

        System.out.println(
            "Docente ficticio insertado correctamente."
        );

        // Al terminar el test, Spring revierte
        // automáticamente esta transacción.
    }
}