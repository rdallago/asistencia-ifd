package com.instituto.asistencia;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ConexionBaseDatosTest {

    @Autowired
    private DataSource dataSource;

    @Test
    void debeConectarConBaseDeDatosDePruebas() throws Exception {

        try (Connection conexion = dataSource.getConnection();
             Statement sentencia = conexion.createStatement();
             ResultSet resultado = sentencia.executeQuery("SELECT DATABASE()")) {

            resultado.next();

            String nombreBaseDatos = resultado.getString(1);

            System.out.println(
                "BASE DE DATOS UTILIZADA POR JUNIT: " + nombreBaseDatos
            );

            assertEquals(
                "asistencia_ifd_test",
                nombreBaseDatos,
                "JUnit no está conectado a la base de pruebas"
            );
        }
    }
}