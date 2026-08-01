package com.instituto.asistencia.service;

import com.instituto.asistencia.entity.AuditoriaRespaldo;
import com.instituto.asistencia.repository.AuditoriaRespaldoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditoriaRespaldoService {

    private final AuditoriaRespaldoRepository auditoriaRepository;

    public AuditoriaRespaldoService(
            AuditoriaRespaldoRepository auditoriaRepository) {

        this.auditoriaRepository = auditoriaRepository;
    }

    public AuditoriaRespaldo registrar(
            String usuario,
            String operacion,
            String resultado,
            String archivo,
            String detalle) {

        AuditoriaRespaldo auditoria =
                new AuditoriaRespaldo();

        auditoria.setUsuario(usuario);

        auditoria.setFechaHora(
                LocalDateTime.now()
        );

        auditoria.setOperacion(operacion);

        auditoria.setResultado(resultado);

        auditoria.setArchivo(archivo);

        auditoria.setDetalle(detalle);

        return auditoriaRepository.save(auditoria);
    }

    public List<AuditoriaRespaldo> listar() {

        return auditoriaRepository
                .findAllByOrderByFechaHoraDesc();
    }
}