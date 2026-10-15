    package com.instituto.asistencia.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Map;
import com.instituto.asistencia.entity.Docente;
import com.instituto.asistencia.repository.DocenteRepository;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;
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
@Test
void debeRechazarDocenteCuandoDniEsInvalido() {

    docenteValido.setDni("12345");

    RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> docenteService.guardar(docenteValido)
    );

    assertEquals(
            "El DNI debe contener solo números y tener entre 7 y 8 dígitos",
            exception.getMessage()
    );

    verify(docenteRepository, never())
            .save(any(Docente.class));

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
void debeRechazarDocenteMenorDe18Anios() {

    docenteValido.setFechaNacimiento(
            LocalDate.now().minusYears(17)
    );

    RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> docenteService.guardar(docenteValido)
    );

    assertEquals(
            "El docente debe ser mayor de 18 años",
            exception.getMessage()
    );

    verify(docenteRepository, never())
            .save(any(Docente.class));

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
void debeRechazarDocenteCuandoDniYaExisteActivo() {

    Docente docenteExistente = new Docente();

    docenteExistente.setDni("12345678");
    docenteExistente.setNombre("Pedro");
    docenteExistente.setApellido("Gómez");
    docenteExistente.setCargo("Profesor");
    docenteExistente.setActivo(true);

    when(docenteRepository.findByDni("12345678"))
            .thenReturn(Optional.of(docenteExistente));

    RuntimeException excepcion = assertThrows(
            RuntimeException.class,
            () -> docenteService.guardar(docenteValido)
    );

    assertEquals(
            "Ya existe un docente activo con ese DNI",
            excepcion.getMessage()
    );

    verify(docenteRepository)
            .findByDni("12345678");

    verify(docenteRepository, never())
            .save(any(Docente.class));

    verifyNoInteractions(auditoriaRespaldoService);
}
@MockitoSettings(strictness = Strictness.LENIENT)
@Test
void debeRechazarDocenteCuandoDniYaExisteYEstaActivo() {

    Docente docenteExistente = new Docente();

    docenteExistente.setDni("12345678");
    docenteExistente.setNombre("Pedro");
    docenteExistente.setApellido("Gómez");
    docenteExistente.setCargo("Profesor");
    docenteExistente.setActivo(true);

    when(docenteRepository.findByDni("12345678"))
            .thenReturn(Optional.of(docenteExistente));

    RuntimeException excepcion = assertThrows(
            RuntimeException.class,
            () -> docenteService.guardar(docenteValido)
    );

    assertEquals(
            "Ya existe un docente activo con ese DNI",
            excepcion.getMessage()
    );

    verify(docenteRepository)
            .findByDni("12345678");

    verify(docenteRepository, never())
            .save(any(Docente.class));

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
@MockitoSettings(strictness = Strictness.LENIENT)
@Test
void debeReactivarDocenteCuandoDniExistePeroEstaInactivo() {

    SecurityContext securityContext =
        SecurityContextHolder.createEmptyContext();

when(authentication.getName())
        .thenReturn("usuarioTest");

securityContext.setAuthentication(authentication);

SecurityContextHolder.setContext(securityContext);

    Docente docenteExistente = new Docente();

    docenteExistente.setDni("12345678");
    docenteExistente.setNombre("Pedro");
    docenteExistente.setApellido("Gómez");
    docenteExistente.setCargo("Profesor");
    docenteExistente.setActivo(false);

    when(docenteRepository.findByDni("12345678"))
            .thenReturn(Optional.of(docenteExistente));

    when(docenteRepository.save(any(Docente.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

    Map<String, Object> respuesta =
            docenteService.guardar(docenteValido);

    Docente resultado =
            (Docente) respuesta.get("docente");

    assertNotNull(resultado);

    assertTrue(
            resultado.getActivo()
    );

    assertEquals(
            "Docente reactivado correctamente",
            respuesta.get("mensaje")
    );

    verify(docenteRepository)
            .findByDni("12345678");

    verify(docenteRepository)
            .save(docenteExistente);

    verify(auditoriaRespaldoService)
            .registrar(
                    anyString(),
                    eq("DOCENTES"),
                    eq("REACTIVACION"),
                    eq("EXITOSO"),
                    isNull(),
                    contains("Se reactivó el docente")
            );
}
@MockitoSettings(strictness = Strictness.LENIENT)
@Test
void debeModificarDocenteCorrectamente() {
    SecurityContext securityContext =
        SecurityContextHolder.createEmptyContext();

    when(authentication.getName())
            .thenReturn("usuarioTest");

    securityContext.setAuthentication(authentication);

    SecurityContextHolder.setContext(securityContext);

    Docente docenteExistente = new Docente();

    docenteExistente.setDni("12345678");
    docenteExistente.setNombre("Juan");
    docenteExistente.setApellido("Pérez");
    docenteExistente.setCargo("Profesor");
    docenteExistente.setDireccion("Calle 123");
    docenteExistente.setTelefono("3794123456");
    docenteExistente.setCorreo("juan@example.com");
    docenteExistente.setFechaNacimiento(
            LocalDate.now().minusYears(30)
    );
    docenteExistente.setActivo(true);

    docenteValido.setNombre("Carlos");
    docenteValido.setApellido("Gómez");
    docenteValido.setCargo("Profesor Titular");
    docenteValido.setDireccion("Nueva dirección 456");
    docenteValido.setTelefono("3794555555");
    docenteValido.setCorreo("carlos@example.com");

    when(docenteRepository.findById(1L))
            .thenReturn(Optional.of(docenteExistente));

    when(docenteRepository.existsByDni("12345678"))
            .thenReturn(true);

    when(docenteRepository.save(any(Docente.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

    Docente resultado =
            docenteService.actualizar(
                    1L,
                    docenteValido
            );

    assertNotNull(resultado);

    assertEquals(
            "Carlos",
            resultado.getNombre()
    );

    assertEquals(
            "Gómez",
            resultado.getApellido()
    );

    assertEquals(
            "Profesor Titular",
            resultado.getCargo()
    );

    assertEquals(
            "Nueva dirección 456",
            resultado.getDireccion()
    );

    assertEquals(
            "3794555555",
            resultado.getTelefono()
    );

    assertEquals(
            "carlos@example.com",
            resultado.getCorreo()
    );

    assertTrue(
            resultado.getActivo()
    );

    verify(docenteRepository)
            .findById(1L);

    verify(docenteRepository)
            .save(docenteExistente);

    verify(auditoriaRespaldoService)
            .registrar(
                    anyString(),
                    eq("DOCENTES"),
                    eq("MODIFICACION"),
                    eq("EXITOSO"),
                    isNull(),
                    contains("Se modificó el docente")
            );
}
@MockitoSettings(strictness = Strictness.LENIENT)
@Test
void debeRechazarModificacionCuandoNuevoDniYaExiste() {

    SecurityContext securityContext =
            SecurityContextHolder.createEmptyContext();

    when(authentication.getName())
            .thenReturn("usuarioTest");

    securityContext.setAuthentication(authentication);

    SecurityContextHolder.setContext(securityContext);

    Docente docenteExistente = new Docente();

    docenteExistente.setDni("12345678");
    docenteExistente.setNombre("Juan");
    docenteExistente.setApellido("Pérez");
    docenteExistente.setCargo("Profesor");
    docenteExistente.setFechaNacimiento(
            LocalDate.now().minusYears(30)
    );
    docenteExistente.setActivo(true);

    docenteValido.setDni("87654321");

    when(docenteRepository.findById(1L))
            .thenReturn(Optional.of(docenteExistente));

    when(docenteRepository.existsByDni("87654321"))
            .thenReturn(true);

    RuntimeException excepcion =
            assertThrows(
                    RuntimeException.class,
                    () -> docenteService.actualizar(
                            1L,
                            docenteValido
                    )
            );

    assertEquals(
            "Ya existe un docente con ese DNI",
            excepcion.getMessage()
    );

    verify(docenteRepository)
            .findById(1L);

    verify(docenteRepository)
            .existsByDni("87654321");

    verify(docenteRepository, never())
            .save(any(Docente.class));

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
@MockitoSettings(strictness = Strictness.LENIENT)
@Test
void debeDesactivarDocenteCorrectamente() {

    SecurityContext securityContext =
            SecurityContextHolder.createEmptyContext();

    when(authentication.getName())
            .thenReturn("usuarioTest");

    securityContext.setAuthentication(authentication);

    SecurityContextHolder.setContext(securityContext);

    Docente docenteExistente = new Docente();

    docenteExistente.setDni("12345678");
    docenteExistente.setNombre("Juan");
    docenteExistente.setApellido("Pérez");
    docenteExistente.setCargo("Profesor");
    docenteExistente.setFechaNacimiento(
            LocalDate.now().minusYears(30)
    );
    docenteExistente.setActivo(true);

    when(docenteRepository.findById(1L))
            .thenReturn(Optional.of(docenteExistente));

    when(docenteRepository.save(any(Docente.class)))
            .thenAnswer(invocation ->
                    invocation.getArgument(0));

    docenteService.eliminar(1L);

    assertFalse(
            docenteExistente.getActivo()
    );

    verify(docenteRepository)
            .findById(1L);

    verify(docenteRepository)
            .save(docenteExistente);

    verify(auditoriaRespaldoService)
            .registrar(
                    anyString(),
                    eq("DOCENTES"),
                    eq("BAJA"),
                    eq("EXITOSO"),
                    isNull(),
                    contains("Se desactivó el docente")
            );
}
@MockitoSettings(strictness = Strictness.LENIENT)
@Test
void debeRechazarBusquedaCuandoDocenteNoExiste() {

    when(docenteRepository.findById(999L))
            .thenReturn(Optional.empty());

    RuntimeException excepcion =
            assertThrows(
                    RuntimeException.class,
                    () -> docenteService.buscarPorId(999L)
            );

    assertEquals(
            "No existe docente ID: 999",
            excepcion.getMessage()
    );

    verify(docenteRepository)
            .findById(999L);
}
@MockitoSettings(strictness = Strictness.LENIENT)
@Test
void debeListarSoloDocentesActivos() {

    Docente docente1 = new Docente();

    docente1.setDni("12345678");
    docente1.setNombre("Juan");
    docente1.setApellido("Pérez");
    docente1.setCargo("Profesor");
    docente1.setActivo(true);


    Docente docente2 = new Docente();

    docente2.setDni("87654321");
    docente2.setNombre("María");
    docente2.setApellido("Gómez");
    docente2.setCargo("Profesora");
    docente2.setActivo(true);


    when(docenteRepository.findByActivoTrue())
            .thenReturn(List.of(
                    docente1,
                    docente2
            ));


    List<Docente> resultado =
            docenteService.listarTodos();


    assertNotNull(resultado);

    assertEquals(
            2,
            resultado.size()
    );

    assertEquals(
            "Juan",
            resultado.get(0).getNombre()
    );

    assertEquals(
            "María",
            resultado.get(1).getNombre()
    );

    assertTrue(
            resultado.get(0).getActivo()
    );

    assertTrue(
            resultado.get(1).getActivo()
    );


    verify(docenteRepository)
            .findByActivoTrue();
}
@MockitoSettings(strictness = Strictness.LENIENT)
@Test
void debeListarDocentesOrdenadosParaReporte() {

    Docente docente1 = new Docente();

    docente1.setDni("11111111");
    docente1.setNombre("Carlos");
    docente1.setApellido("Gómez");
    docente1.setCargo("Profesor");
    docente1.setActivo(true);


    Docente docente2 = new Docente();

    docente2.setDni("22222222");
    docente2.setNombre("Ana");
    docente2.setApellido("Pérez");
    docente2.setCargo("Profesora");
    docente2.setActivo(true);


    Docente docente3 = new Docente();

    docente3.setDni("33333333");
    docente3.setNombre("Luis");
    docente3.setApellido("Rodríguez");
    docente3.setCargo("Profesor");
    docente3.setActivo(true);


    List<Docente> docentesOrdenados =
            List.of(
                    docente1,
                    docente2,
                    docente3
            );


    when(docenteRepository
            .findAllByOrderByApellidoAscNombreAsc())
            .thenReturn(docentesOrdenados);


    List<Docente> resultado =
            docenteService.listarParaReporte();


    assertNotNull(resultado);

    assertEquals(
            3,
            resultado.size()
    );


    assertEquals(
            "Gómez",
            resultado.get(0).getApellido()
    );

    assertEquals(
            "Pérez",
            resultado.get(1).getApellido()
    );

    assertEquals(
            "Rodríguez",
            resultado.get(2).getApellido()
    );


    verify(docenteRepository)
            .findAllByOrderByApellidoAscNombreAsc();
}
@MockitoSettings(strictness = Strictness.LENIENT)
@Test
void debeModificarDocenteSinCambiarDni() {

    SecurityContext securityContext =
            SecurityContextHolder.createEmptyContext();

    when(authentication.getName())
            .thenReturn("usuarioTest");

    securityContext.setAuthentication(authentication);

    SecurityContextHolder.setContext(securityContext);


    Docente docenteExistente = new Docente();

    docenteExistente.setDni("12345678");
    docenteExistente.setNombre("Juan");
    docenteExistente.setApellido("Pérez");
    docenteExistente.setCargo("Profesor");
    docenteExistente.setDireccion("Calle 123");
    docenteExistente.setTelefono("3794000000");
    docenteExistente.setCorreo("juan@example.com");
    docenteExistente.setFechaNacimiento(
            LocalDate.now().minusYears(30)
    );
    docenteExistente.setActivo(true);


    Docente docenteActualizado = new Docente();

    docenteActualizado.setDni("12345678");
    docenteActualizado.setNombre("Juan Carlos");
    docenteActualizado.setApellido("Pérez");
    docenteActualizado.setCargo("Profesor Titular");
    docenteActualizado.setDireccion("Calle 456");
    docenteActualizado.setTelefono("3794111111");
    docenteActualizado.setCorreo("juancarlos@example.com");
    docenteActualizado.setFechaNacimiento(
            LocalDate.now().minusYears(30)
    );
    docenteActualizado.setActivo(true);


    when(docenteRepository.findById(1L))
            .thenReturn(Optional.of(docenteExistente));


    when(docenteRepository.save(any(Docente.class)))
            .thenAnswer(invocation ->
                    invocation.getArgument(0));


    Docente resultado =
            docenteService.actualizar(
                    1L,
                    docenteActualizado
            );


    assertNotNull(resultado);


    assertEquals(
            "12345678",
            resultado.getDni()
    );

    assertEquals(
            "Juan Carlos",
            resultado.getNombre()
    );

    assertEquals(
            "Pérez",
            resultado.getApellido()
    );

    assertEquals(
            "Profesor Titular",
            resultado.getCargo()
    );

    assertEquals(
            "Calle 456",
            resultado.getDireccion()
    );

    assertEquals(
            "3794111111",
            resultado.getTelefono()
    );

    assertEquals(
            "juancarlos@example.com",
            resultado.getCorreo()
    );


    assertTrue(
            resultado.getActivo()
    );


    verify(docenteRepository)
            .findById(1L);


    verify(docenteRepository, never())
            .existsByDni(anyString());


    verify(docenteRepository)
            .save(docenteExistente);


    verify(auditoriaRespaldoService)
            .registrar(
                    anyString(),
                    eq("DOCENTES"),
                    eq("MODIFICACION"),
                    eq("EXITOSO"),
                    isNull(),
                    contains("Se modificó el docente")
            );
}
@MockitoSettings(strictness = Strictness.LENIENT)
@Test
void debeModificarDocenteCambiandoDniCuandoNuevoDniEstaDisponible() {

    SecurityContext securityContext =
            SecurityContextHolder.createEmptyContext();

    when(authentication.getName())
            .thenReturn("usuarioTest");

    securityContext.setAuthentication(authentication);

    SecurityContextHolder.setContext(securityContext);


    Docente docenteExistente = new Docente();

    docenteExistente.setDni("12345678");
    docenteExistente.setNombre("Juan");
    docenteExistente.setApellido("Pérez");
    docenteExistente.setCargo("Profesor");
    docenteExistente.setDireccion("Calle 123");
    docenteExistente.setTelefono("3794000000");
    docenteExistente.setCorreo("juan@example.com");
    docenteExistente.setFechaNacimiento(
            LocalDate.now().minusYears(30)
    );
    docenteExistente.setActivo(true);


    Docente docenteActualizado = new Docente();

    docenteActualizado.setDni("87654321");
    docenteActualizado.setNombre("Juan Carlos");
    docenteActualizado.setApellido("Pérez");
    docenteActualizado.setCargo("Profesor Titular");
    docenteActualizado.setDireccion("Calle 456");
    docenteActualizado.setTelefono("3794111111");
    docenteActualizado.setCorreo("juancarlos@example.com");
    docenteActualizado.setFechaNacimiento(
            LocalDate.now().minusYears(30)
    );
    docenteActualizado.setActivo(true);


    when(docenteRepository.findById(1L))
            .thenReturn(Optional.of(docenteExistente));


    when(docenteRepository.existsByDni("87654321"))
            .thenReturn(false);


    when(docenteRepository.save(any(Docente.class)))
            .thenAnswer(invocation ->
                    invocation.getArgument(0));


    Docente resultado =
            docenteService.actualizar(
                    1L,
                    docenteActualizado
            );


    assertNotNull(resultado);


    assertEquals(
            "87654321",
            resultado.getDni()
    );

    assertEquals(
            "Juan Carlos",
            resultado.getNombre()
    );

    assertEquals(
            "Pérez",
            resultado.getApellido()
    );

    assertEquals(
            "Profesor Titular",
            resultado.getCargo()
    );

    assertEquals(
            "Calle 456",
            resultado.getDireccion()
    );

    assertEquals(
            "3794111111",
            resultado.getTelefono()
    );

    assertEquals(
            "juancarlos@example.com",
            resultado.getCorreo()
    );

    assertTrue(
            resultado.getActivo()
    );


    verify(docenteRepository)
            .findById(1L);


    verify(docenteRepository)
            .existsByDni("87654321");


    verify(docenteRepository)
            .save(docenteExistente);


    verify(auditoriaRespaldoService)
            .registrar(
                    anyString(),
                    eq("DOCENTES"),
                    eq("MODIFICACION"),
                    eq("EXITOSO"),
                    isNull(),
                    contains("Se modificó el docente")
            );
}
@MockitoSettings(strictness = Strictness.LENIENT)
@Test
void debeRechazarDocenteCuandoDniTieneLetras() {

    Docente docenteInvalido = new Docente();

    docenteInvalido.setDni("1234ABCD");
    docenteInvalido.setNombre("Juan");
    docenteInvalido.setApellido("Pérez");
    docenteInvalido.setCargo("Profesor");
    docenteInvalido.setDireccion("Calle 123");
    docenteInvalido.setTelefono("3794123456");
    docenteInvalido.setCorreo("juan@example.com");

    docenteInvalido.setFechaNacimiento(
            LocalDate.now().minusYears(30)
    );


    RuntimeException excepcion =
            assertThrows(
                    RuntimeException.class,
                    () -> docenteService.guardar(docenteInvalido)
            );


    assertEquals(
            "El DNI debe contener solo números y tener entre 7 y 8 dígitos",
            excepcion.getMessage()
    );


    verify(
            docenteRepository,
            never()
    ).findByDni(anyString());


    verify(
            docenteRepository,
            never()
    ).save(any(Docente.class));


    verifyNoInteractions(
            auditoriaRespaldoService
    );
}
@MockitoSettings(strictness = Strictness.LENIENT)
@Test
void debeRechazarDocenteCuandoDniTieneMenosDeSieteDigitos() {

    Docente docenteInvalido = new Docente();

    docenteInvalido.setDni("123456");
    docenteInvalido.setNombre("Juan");
    docenteInvalido.setApellido("Pérez");
    docenteInvalido.setCargo("Profesor");
    docenteInvalido.setDireccion("Calle 123");
    docenteInvalido.setTelefono("3794123456");
    docenteInvalido.setCorreo("juan@example.com");

    docenteInvalido.setFechaNacimiento(
            LocalDate.now().minusYears(30)
    );


    RuntimeException excepcion =
            assertThrows(
                    RuntimeException.class,
                    () -> docenteService.guardar(docenteInvalido)
            );


    assertEquals(
            "El DNI debe contener solo números y tener entre 7 y 8 dígitos",
            excepcion.getMessage()
    );


    verify(
            docenteRepository,
            never()
    ).findByDni(anyString());


    verify(
            docenteRepository,
            never()
    ).save(any(Docente.class));


    verifyNoInteractions(
            auditoriaRespaldoService
    );
}
@MockitoSettings(strictness = Strictness.LENIENT)
@Test
void debeRechazarDocenteCuandoDniTieneMasDeOchoDigitos() {

    Docente docenteInvalido = new Docente();

    docenteInvalido.setDni("123456789");
    docenteInvalido.setNombre("Juan");
    docenteInvalido.setApellido("Pérez");
    docenteInvalido.setCargo("Profesor");
    docenteInvalido.setDireccion("Calle 123");
    docenteInvalido.setTelefono("3794123456");
    docenteInvalido.setCorreo("juan@example.com");

    docenteInvalido.setFechaNacimiento(
            LocalDate.now().minusYears(30)
    );


    RuntimeException excepcion =
            assertThrows(
                    RuntimeException.class,
                    () -> docenteService.guardar(docenteInvalido)
            );


    assertEquals(
            "El DNI debe contener solo números y tener entre 7 y 8 dígitos",
            excepcion.getMessage()
    );


    verify(
            docenteRepository,
            never()
    ).findByDni(anyString());


    verify(
            docenteRepository,
            never()
    ).save(any(Docente.class));


    verifyNoInteractions(
            auditoriaRespaldoService
    );
}
@MockitoSettings(strictness = Strictness.LENIENT)
@Test
void debeRechazarDocenteCuandoNombreEstaVacio() {

    Docente docenteInvalido = new Docente();

    docenteInvalido.setDni("12345678");
    docenteInvalido.setNombre("");
    docenteInvalido.setApellido("Pérez");
    docenteInvalido.setCargo("Profesor");
    docenteInvalido.setDireccion("Calle 123");
    docenteInvalido.setTelefono("3794123456");
    docenteInvalido.setCorreo("juan@example.com");

    docenteInvalido.setFechaNacimiento(
            LocalDate.now().minusYears(30)
    );

    RuntimeException excepcion =
            assertThrows(
                    RuntimeException.class,
                    () -> docenteService.guardar(docenteInvalido)
            );

    assertEquals(
            "El nombre es obligatorio",
            excepcion.getMessage()
    );

    verify(
            docenteRepository,
            never()
    ).findByDni(anyString());

    verify(
            docenteRepository,
            never()
    ).save(any(Docente.class));

    verifyNoInteractions(
            auditoriaRespaldoService
    );
}
@MockitoSettings(strictness = Strictness.LENIENT)
@Test
void debeRechazarDocenteCuandoApellidoEstaVacio() {

    Docente docenteInvalido = new Docente();

    docenteInvalido.setDni("12345678");
    docenteInvalido.setNombre("Juan");
    docenteInvalido.setApellido("");
    docenteInvalido.setCargo("Profesor");
    docenteInvalido.setDireccion("Calle 123");
    docenteInvalido.setTelefono("3794123456");
    docenteInvalido.setCorreo("juan@example.com");

    docenteInvalido.setFechaNacimiento(
            LocalDate.now().minusYears(30)
    );

    RuntimeException excepcion =
            assertThrows(
                    RuntimeException.class,
                    () -> docenteService.guardar(docenteInvalido)
            );

    assertEquals(
            "El apellido es obligatorio",
            excepcion.getMessage()
    );

    verify(
            docenteRepository,
            never()
    ).findByDni(anyString());

    verify(
            docenteRepository,
            never()
    ).save(any(Docente.class));

    verifyNoInteractions(
            auditoriaRespaldoService
    );
}
@MockitoSettings(strictness = Strictness.LENIENT)
@Test
void debeRechazarDocenteCuandoCargoEstaVacio() {

    Docente docenteInvalido = new Docente();

    docenteInvalido.setDni("12345678");
    docenteInvalido.setNombre("Juan");
    docenteInvalido.setApellido("Pérez");
    docenteInvalido.setCargo("");
    docenteInvalido.setDireccion("Calle 123");
    docenteInvalido.setTelefono("3794123456");
    docenteInvalido.setCorreo("juan@example.com");

    docenteInvalido.setFechaNacimiento(
            LocalDate.now().minusYears(30)
    );

    RuntimeException excepcion =
            assertThrows(
                    RuntimeException.class,
                    () -> docenteService.guardar(docenteInvalido)
            );

    assertEquals(
            "El cargo es obligatorio",
            excepcion.getMessage()
    );

    verify(
            docenteRepository,
            never()
    ).findByDni(anyString());

    verify(
            docenteRepository,
            never()
    ).save(any(Docente.class));

    verifyNoInteractions(
            auditoriaRespaldoService
    );
}
@MockitoSettings(strictness = Strictness.LENIENT)
@Test
void debeRechazarDocenteCuandoDireccionSupera250Caracteres() {

    Docente docenteInvalido = new Docente();

    docenteInvalido.setDni("12345678");
    docenteInvalido.setNombre("Juan");
    docenteInvalido.setApellido("Pérez");
    docenteInvalido.setCargo("Profesor");

    String direccionLarga = "A".repeat(251);

    docenteInvalido.setDireccion(direccionLarga);

    docenteInvalido.setTelefono("3794123456");
    docenteInvalido.setCorreo("juan@example.com");

    docenteInvalido.setFechaNacimiento(
            LocalDate.now().minusYears(30)
    );

    RuntimeException excepcion =
            assertThrows(
                    RuntimeException.class,
                    () -> docenteService.guardar(docenteInvalido)
            );

    assertEquals(
            "La dirección no puede superar los 250 caracteres",
            excepcion.getMessage()
    );

    verify(
            docenteRepository,
            never()
    ).findByDni(anyString());

    verify(
            docenteRepository,
            never()
    ).save(any(Docente.class));

    verifyNoInteractions(
            auditoriaRespaldoService
    );
}
@MockitoSettings(strictness = Strictness.LENIENT)
@Test
void debeRechazarDocenteCuandoFechaNacimientoEsNula() {

    Docente docenteInvalido = new Docente();

    docenteInvalido.setDni("12345678");
    docenteInvalido.setNombre("Juan");
    docenteInvalido.setApellido("Pérez");
    docenteInvalido.setCargo("Profesor");
    docenteInvalido.setDireccion("Calle 123");
    docenteInvalido.setTelefono("3794123456");
    docenteInvalido.setCorreo("juan@example.com");

    docenteInvalido.setFechaNacimiento(null);

    RuntimeException excepcion =
            assertThrows(
                    RuntimeException.class,
                    () -> docenteService.guardar(docenteInvalido)
            );

    assertEquals(
            "La fecha de nacimiento es obligatoria",
            excepcion.getMessage()
    );

    verify(
            docenteRepository,
            never()
    ).findByDni(anyString());

    verify(
            docenteRepository,
            never()
    ).save(any(Docente.class));

    verifyNoInteractions(
            auditoriaRespaldoService
    );
}
@MockitoSettings(strictness = Strictness.LENIENT)
@Test
void debeListarSolamenteDocentesActivos() {

    Docente docente1 = new Docente();
    docente1.setId(1L);
    docente1.setDni("12345678");
    docente1.setNombre("Juan");
    docente1.setApellido("Pérez");
    docente1.setActivo(true);


    Docente docente2 = new Docente();
    docente2.setId(2L);
    docente2.setDni("87654321");
    docente2.setNombre("Ana");
    docente2.setApellido("Gómez");
    docente2.setActivo(true);


    when(docenteRepository.findByActivoTrue())
            .thenReturn(
                    List.of(docente1, docente2)
            );


    List<Docente> resultado =
            docenteService.listarTodos();


    assertNotNull(resultado);

    assertEquals(
            2,
            resultado.size()
    );

    assertTrue(
            resultado.stream()
                    .allMatch(Docente::getActivo)
    );


    verify(docenteRepository)
            .findByActivoTrue();
}
@MockitoSettings(strictness = Strictness.LENIENT)
@Test
void debeListarTodosLosDocentes() {

    Docente docente1 = new Docente();
    docente1.setId(1L);
    docente1.setDni("12345678");
    docente1.setNombre("Juan");
    docente1.setApellido("Pérez");


    Docente docente2 = new Docente();
    docente2.setId(2L);
    docente2.setDni("87654321");
    docente2.setNombre("Ana");
    docente2.setApellido("Gómez");


    when(docenteRepository.findAll())
            .thenReturn(
                    List.of(docente1, docente2)
            );


    List resultado =
            docenteService.listar();


    assertNotNull(resultado);

    assertEquals(
            2,
            resultado.size()
    );


    verify(docenteRepository)
            .findAll();
}

@Test
void debeRechazarDocenteCuandoCorreoEsInvalido() {
    
    
    docenteValido.setCorreo("juanexample.com");

    RuntimeException exception =
            assertThrows(
                    RuntimeException.class,
                    () -> docenteService.guardar(docenteValido)
            );

    assertEquals(
            "El correo no es válido",
            exception.getMessage()
    );

    verify(docenteRepository, never())
            .findByDni(anyString());

    verify(docenteRepository, never())
            .save(any(Docente.class));

    verifyNoInteractions(auditoriaRespaldoService);
}
@Test
void debeRechazarModificacionCuandoCorreoEsInvalido() {

    Docente docenteExistente = new Docente();

    docenteExistente.setDni("12345678");
    docenteExistente.setNombre("Juan");
    docenteExistente.setApellido("Pérez");
    docenteExistente.setCargo("Profesor");
    docenteExistente.setFechaNacimiento(
            LocalDate.now().minusYears(30)
    );
    docenteExistente.setActivo(true);
    docenteExistente.setCorreo("juan@gmail.com");

    Docente docenteActualizado = new Docente();

    docenteActualizado.setDni("12345678");
    docenteActualizado.setNombre("Juan");
    docenteActualizado.setApellido("Pérez");
    docenteActualizado.setCargo("Profesor");
    docenteActualizado.setFechaNacimiento(
            LocalDate.now().minusYears(30)
    );

    // Correo inválido
    docenteActualizado.setCorreo("juangmail.com");

    when(docenteRepository.findById(1L))
            .thenReturn(Optional.of(docenteExistente));

    RuntimeException excepcion =
            assertThrows(
                    RuntimeException.class,
                    () -> docenteService.actualizar(
                            1L,
                            docenteActualizado
                    )
            );

    assertEquals(
            "El correo no es válido",
            excepcion.getMessage()
    );

    verify(docenteRepository)
            .findById(1L);

    verify(docenteRepository, never())
            .save(any(Docente.class));

    verifyNoInteractions(auditoriaRespaldoService);
}
@Test
void debeRechazarModificacionCuandoDocenteNoExiste() {

    Docente docenteActualizado = new Docente();

    docenteActualizado.setDni("12345678");
    docenteActualizado.setNombre("Juan");
    docenteActualizado.setApellido("Pérez");
    docenteActualizado.setCargo("Profesor");
    docenteActualizado.setFechaNacimiento(
            LocalDate.now().minusYears(30)
    );

    when(docenteRepository.findById(999L))
            .thenReturn(Optional.empty());

    RuntimeException excepcion =
            assertThrows(
                    RuntimeException.class,
                    () -> docenteService.actualizar(
                            999L,
                            docenteActualizado
                    )
            );

    assertEquals(
            "No existe docente ID: 999",
            excepcion.getMessage()
    );

    verify(docenteRepository)
            .findById(999L);

    verify(docenteRepository, never())
            .save(any(Docente.class));

    verifyNoInteractions(auditoriaRespaldoService);
}
@Test
void debeRechazarDocenteCuandoFechaNacimientoEsFutura() {

    docenteValido.setFechaNacimiento(
            LocalDate.now().plusDays(1)
    );

    RuntimeException excepcion =
            assertThrows(
                    RuntimeException.class,
                    () -> docenteService.guardar(docenteValido)
            );

    assertEquals(
            "La fecha de nacimiento no puede ser futura",
            excepcion.getMessage()
    );

    verify(
            docenteRepository,
            never()
    ).findByDni(anyString());

    verify(
            docenteRepository,
            never()
    ).save(any(Docente.class));

    verifyNoInteractions(
            auditoriaRespaldoService
    );
}
@Test
void debeAceptarDocenteConExactamente18Anios() {
    
    SecurityContext securityContext =
            SecurityContextHolder.createEmptyContext();

    when(authentication.getName())
            .thenReturn("usuarioTest");

    securityContext.setAuthentication(authentication);

    SecurityContextHolder.setContext(securityContext);

    docenteValido.setFechaNacimiento(
            LocalDate.now().minusYears(18)
    );

    when(docenteRepository.findByDni("12345678"))
            .thenReturn(Optional.empty());

    when(docenteRepository.save(any(Docente.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

    // configurar Authentication igual que en tu test de alta

    Map<String, Object> respuesta =
            docenteService.guardar(docenteValido);

    assertNotNull(respuesta.get("docente"));

    Docente resultado =
            (Docente) respuesta.get("docente");

    assertTrue(resultado.getActivo());

    verify(docenteRepository)
            .save(any(Docente.class));
}
}