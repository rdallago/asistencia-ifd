package com.instituto.asistencia.service;

import com.instituto.asistencia.entity.Institucion;
import com.instituto.asistencia.repository.InstitucionRepository;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InstitucionServiceTest {

    @Mock
    private InstitucionRepository institucionRepository;

    @InjectMocks
    private InstitucionService institucionService;


    @Test
void debeListarTodasLasInstituciones() {

    Institucion institucion1 = new Institucion();
    institucion1.setId(1L);

    Institucion institucion2 = new Institucion();
    institucion2.setId(2L);

    when(institucionRepository.findAll())
            .thenReturn(List.of(institucion1, institucion2));

    List<Institucion> resultado =
            institucionService.listarTodas();

    assertNotNull(resultado);
    assertEquals(2, resultado.size());
    assertEquals(1L, resultado.get(0).getId());
    assertEquals(2L, resultado.get(1).getId());

    verify(institucionRepository)
            .findAll();
}
@Test
void debeBuscarInstitucionPorId() {

    Institucion institucion = new Institucion();
    institucion.setId(1L);

    when(institucionRepository.findById(1L))
            .thenReturn(Optional.of(institucion));

    Institucion resultado =
            institucionService.buscarPorId(1L);

    assertNotNull(resultado);
    assertEquals(1L, resultado.getId());

    verify(institucionRepository)
            .findById(1L);
}
@Test
void debeLanzarExcepcionCuandoInstitucionNoExiste() {

    when(institucionRepository.findById(999L))
            .thenReturn(Optional.empty());

    RuntimeException excepcion =
            assertThrows(
                    RuntimeException.class,
                    () -> institucionService.buscarPorId(999L)
            );

    assertEquals(
            "Institución no encontrada",
            excepcion.getMessage()
    );

    verify(institucionRepository)
            .findById(999L);
}
@Test
void debeGuardarInstitucionCorrectamente() {

    Institucion institucion = new Institucion();
    institucion.setId(1L);

    when(institucionRepository.save(institucion))
            .thenReturn(institucion);

    Institucion resultado =
            institucionService.guardar(institucion);

    assertNotNull(resultado);
    assertEquals(1L, resultado.getId());

    verify(institucionRepository)
            .save(institucion);
}
}