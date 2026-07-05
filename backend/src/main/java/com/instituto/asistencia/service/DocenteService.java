package com.instituto.asistencia.service;

import java.time.LocalDate;
import com.instituto.asistencia.entity.Docente;
import com.instituto.asistencia.repository.DocenteRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.time.Period;
import java.util.Map;

import java.util.HashMap;
@Service
public class DocenteService {

    private final DocenteRepository docenteRepository;

    public DocenteService(DocenteRepository docenteRepository) {
        this.docenteRepository = docenteRepository;
    }

    // ============================
    // LISTAR
    // ============================

    public List<Docente> listarTodos() {
        return docenteRepository.findByActivoTrue();
    }

    // ============================
    // BUSCAR POR ID
    // ============================

   /* public Docente buscarPorId(Long id) {

        return docenteRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Docente no encontrado"));
    }*/

    // ============================
    // CREAR
    // ============================

public Map<String, Object> guardar(Docente docente) {

    validar(docente);

    Optional<Docente> docenteExistente =
            docenteRepository.findByDni(docente.getDni());

    Map<String, Object> respuesta = new HashMap<>();

    if (docenteExistente.isPresent()) {

        Docente existente = docenteExistente.get();

        if (existente.getActivo()) {
            throw new RuntimeException(
                    "Ya existe un docente activo con ese DNI");
        }

        existente.setActivo(true);

        // actualizar datos...

        Docente guardado =
                docenteRepository.save(existente);

        respuesta.put("docente", guardado);
        respuesta.put("mensaje",
                "Docente reactivado correctamente");

        return respuesta;
    }

    docente.setFechaAlta(LocalDate.now());
    docente.setActivo(true);

    Docente guardado = docenteRepository.save(docente);

    respuesta.put("docente", guardado);
    respuesta.put("mensaje",
            "Docente creado correctamente");

    return respuesta;
}   
    // ============================
    // MODIFICAR
    // ============================

    public Docente actualizar(
            Long id,
            Docente docenteActualizado) {

        Docente docente = buscarPorId(id);

        validar(docenteActualizado);

        if (!docente.getDni().equals(docenteActualizado.getDni())) {

            if (docenteRepository
                    .existsByDni(docenteActualizado.getDni())) {

                throw new RuntimeException(
                        "Ya existe un docente con ese DNI");
            }
        }

        docente.setNombre(docenteActualizado.getNombre());
        docente.setApellido(docenteActualizado.getApellido());
        docente.setDni(docenteActualizado.getDni());
        docente.setCargo(docenteActualizado.getCargo());
        docente.setDireccion(docenteActualizado.getDireccion());
        docente.setTelefono(docenteActualizado.getTelefono());
        docente.setCorreo(docenteActualizado.getCorreo());
        docente.setFechaNacimiento(docenteActualizado.getFechaNacimiento());
        docente.setActivo(docenteActualizado.getActivo());
        return docenteRepository.save(docente);
    }

    // ============================
    // ELIMINAR
    // ============================
public void eliminar(Long id) {

    Docente docente = buscarPorId(id);

    docente.setActivo(false);

    docenteRepository.save(docente);
}

    // ============================
    // VALIDACIONES
    // ============================

    private void validar(Docente docente) {
    validarMayorEdad(docente.getFechaNacimiento());
    
    if (docente.getDni() == null ||
    docente.getDni().trim().isEmpty()) {

    throw new RuntimeException(
        "El DNI es obligatorio");
}
    
    if (!docente.getDni().matches("\\d{7,8}")) {

    throw new RuntimeException(
        "El DNI debe contener solo números y tener entre 7 y 8 dígitos"
    );
}
        if (docente.getDni() == null ||
                docente.getDni().trim().isEmpty()) {

            throw new RuntimeException(
                    "El DNI es obligatorio");
        }

        if (docente.getNombre() == null ||
                docente.getNombre().trim().isEmpty()) {

            throw new RuntimeException(
                    "El nombre es obligatorio");
        }

        if (docente.getApellido() == null ||
                docente.getApellido().trim().isEmpty()) {

            throw new RuntimeException(
                    "El apellido es obligatorio");
        }

        if (docente.getCargo() == null ||
                docente.getCargo().trim().isEmpty()) {

            throw new RuntimeException(
                    "El cargo es obligatorio");
        }
        if (
    docente.getDireccion() != null &&
    docente.getDireccion().length() > 250
) {

    throw new RuntimeException(
        "La dirección no puede superar los 250 caracteres"
    );
}
    }
    public Docente buscarPorId(Long id) {
    System.out.println("ID RECIBIDO: " + id);

    return docenteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("No existe docente ID: " + id));
}
    private void validarMayorEdad(LocalDate fechaNacimiento) {

    if (fechaNacimiento == null) {
        throw new RuntimeException(
            "La fecha de nacimiento es obligatoria"
        );
    }

    int edad = Period.between(
        fechaNacimiento,
        LocalDate.now()
    ).getYears();

    if (edad < 18) {
        throw new RuntimeException(
            "El docente debe ser mayor de 18 años"
        );
    }
}
    public List<Docente> listar() {
    return docenteRepository.findAll();
}
  
}