package com.instituto.asistencia.service;

import com.instituto.asistencia.entity.Docente;
import com.instituto.asistencia.repository.DocenteRepository;
import java.util.List;
import org.springframework.stereotype.Service;

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
        return docenteRepository.findAll();
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

    public Docente guardar(Docente docente) {

        validar(docente);

        if (docenteRepository.existsByDni(docente.getDni())) {
            throw new RuntimeException(
                    "Ya existe un docente con ese DNI");
        }

        return docenteRepository.save(docente);
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

        return docenteRepository.save(docente);
    }

    // ============================
    // ELIMINAR
    // ============================

    public void eliminar(Long id) {

        Docente docente = buscarPorId(id);

        docenteRepository.delete(docente);
    }

    // ============================
    // VALIDACIONES
    // ============================

    private void validar(Docente docente) {

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
    }
    public Docente buscarPorId(Long id) {
    System.out.println("ID RECIBIDO: " + id);

    return docenteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("No existe docente ID: " + id));
}
}