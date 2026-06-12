package com.instituto.asistencia.repository;

import com.instituto.asistencia.entity.Institucion;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InstitucionRepository
        extends JpaRepository<Institucion, Long> {

}