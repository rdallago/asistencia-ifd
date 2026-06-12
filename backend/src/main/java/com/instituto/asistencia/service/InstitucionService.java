package com.instituto.asistencia.service;

import com.instituto.asistencia.entity.Institucion;
import com.instituto.asistencia.repository.InstitucionRepository;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class InstitucionService {

    private final InstitucionRepository institucionRepository;

    public InstitucionService(
            InstitucionRepository institucionRepository) {

        this.institucionRepository = institucionRepository;
    }

    public List<Institucion> listarTodas() {
        return institucionRepository.findAll();
    }

    public Institucion buscarPorId(Long id) {

        return institucionRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Institución no encontrada"));
    }

    public Institucion guardar(
            Institucion institucion) {

        return institucionRepository.save(
                institucion);
    }
}