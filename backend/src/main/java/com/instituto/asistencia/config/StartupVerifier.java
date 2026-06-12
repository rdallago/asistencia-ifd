package com.instituto.asistencia.config;

import com.instituto.asistencia.service.JornadaService;

import jakarta.annotation.PostConstruct;

import org.springframework.stereotype.Component;

@Component
public class StartupVerifier {

    private final JornadaService jornadaService;

    public StartupVerifier(
            JornadaService jornadaService) {

        this.jornadaService = jornadaService;
    }

    @PostConstruct
    public void verificar() {

        jornadaService.cerrarJornadasPendientes();

    }
}