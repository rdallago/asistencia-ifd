package com.instituto.asistencia.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.instituto.asistencia.entity.Docente;
import com.instituto.asistencia.repository.DocenteRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocenteServiceTest {

    @Mock
    private DocenteRepository docenteRepository;

    @Mock
    private AuditoriaRespaldoService auditoriaRespaldoService;

    @Mock

    private Authentication authentication;
    
    @InjectMocks
    private DocenteService docenteService;

    private Docente docenteValido;

    @BeforeEach
    void prepararDocente() {

        docenteValido = new Docente();

        docenteValido.setDni("12345678");
        docenteValido.setNombre("Juan");
        docenteValido.setApellido("Pérez");
        docenteValido.setCargo("Profesor");
        docenteValido.setDireccion("Calle 123");
        docenteValido.setTelefono("3794123456");
        docenteValido.setCorreo("juan@example.com");

        docenteValido.setFechaNacimiento(
                LocalDate.now().minusYears(30)
        );
    }
    
   @BeforeEach
void prepararSeguridad() {
    SecurityContextHolder.clearContext();
}

@Test
void debeCrearDocenteCuandoDniNoExiste() {

    SecurityContext securityContext =
            SecurityContextHolder.createEmptyContext();

    when(authentication.getName())
            .thenReturn("usuarioTest");

    securityContext.setAuthentication(authentication);

    SecurityContextHolder.setContext(securityContext);

    when(docenteRepository.findByDni("12345678"))
            .thenReturn(Optional.empty());

    when(docenteRepository.save(any(Docente.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

    Docente resultado =
            (Docente) docenteService.guardar(docenteValido)
                    .get("docente");

    assertNotNull(resultado);

    assertEquals(
            "12345678",
            resultado.getDni()
    );

    assertEquals(
            "Juan",
            resultado.getNombre()
    );

    assertEquals(
            "Pérez",
            resultado.getApellido()
    );

    assertTrue(
            resultado.getActivo()
    );

    assertEquals(
            LocalDate.now(),
            resultado.getFechaAlta()
    );

    verify(docenteRepository)
            .findByDni("12345678");

    verify(docenteRepository)
            .save(any(Docente.class));

    verify(auditoriaRespaldoService)
            .registrar(
                    anyString(),
                    eq("DOCENTES"),
                    eq("ALTA"),
                    eq("EXITOSO"),
                    isNull(),
                    contains("Se creó el docente")
            );
}
    @Test
void debeRechazarDocenteCuandoDniEstaVacio() {

    docenteValido.setDni("");

    RuntimeException excepcion =
            assertThrows(
                    RuntimeException.class,
                    () -> docenteService.guardar(docenteValido)
            );

    assertEquals(
            "El DNI es obligatorio",
            excepcion.getMessage()
    );

    verify(docenteRepository, never())
            .save(any(Docente.class));
}
}