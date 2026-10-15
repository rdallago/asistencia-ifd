package com.instituto.asistencia.service;

import com.instituto.asistencia.entity.AuditoriaRespaldo;
import com.instituto.asistencia.repository.AuditoriaRespaldoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditoriaRespaldoService {

    private final AuditoriaRespaldoRepository auditoriaRepository;

    public AuditoriaRespaldoService(
            AuditoriaRespaldoRepository auditoriaRepository) {

        this.auditoriaRepository = auditoriaRepository;
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AuditoriaRespaldo registrar(
        String usuario,
        String modulo,
        String accion,
        String resultado,
        String archivo,
        String detalle) {

    AuditoriaRespaldo auditoria =
            new AuditoriaRespaldo();

    auditoria.setUsuario(usuario);

    auditoria.setFechaHora(
            LocalDateTime.now()
    );

    auditoria.setModulo(modulo);

    auditoria.setAccion(accion);
    

    auditoria.setResultado(resultado);

    auditoria.setArchivo(archivo);

    auditoria.setDetalle(detalle);

    return auditoriaRepository.save(auditoria);
}

    public List<AuditoriaRespaldo> listar() {

        return auditoriaRepository
                .findAllByOrderByFechaHoraDesc();
    }
    
public List<AuditoriaRespaldo> listarPorModulo(String modulo) {

    if ("RESPALDOS".equals(modulo)) {

        return auditoriaRepository.findAllByOrderByFechaHoraDesc();

    }

    return auditoriaRepository.findByModuloOrderByFechaHoraDesc(modulo);
}
@Transactional(propagation = Propagation.MANDATORY)
public AuditoriaRespaldo registrarEnTransaccion(
        String usuario,
        String modulo,
        String accion,
        String resultado,
        String archivo,
        String detalle) {

    AuditoriaRespaldo auditoria = new AuditoriaRespaldo();

    auditoria.setUsuario(usuario);
    auditoria.setFechaHora(LocalDateTime.now());
    auditoria.setModulo(modulo);
    auditoria.setAccion(accion);
    auditoria.setResultado(resultado);
    auditoria.setArchivo(archivo);
    auditoria.setDetalle(detalle);

    return auditoriaRepository.save(auditoria);
}
}