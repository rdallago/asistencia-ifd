package com.instituto.asistencia.repository;

import com.instituto.asistencia.entity.AuditoriaRespaldo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditoriaRespaldoRepository
        extends JpaRepository<AuditoriaRespaldo, Long> {

    List<AuditoriaRespaldo> findAllByOrderByFechaHoraDesc();

    List<AuditoriaRespaldo> findByUsuarioOrderByFechaHoraDesc(
            String usuario
    );
    List<AuditoriaRespaldo> findByModuloOrderByFechaHoraDesc(
        String modulo);
}