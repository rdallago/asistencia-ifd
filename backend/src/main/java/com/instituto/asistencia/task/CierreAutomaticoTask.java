package com.instituto.asistencia.task;

import com.instituto.asistencia.service.JornadaService;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
@Component
public class CierreAutomaticoTask {

    private final JornadaService jornadaService;

    public CierreAutomaticoTask(
            JornadaService jornadaService) {

        this.jornadaService = jornadaService;
    }

    @Scheduled(cron = "5 0 0 * * *")
    public void ejecutar() {

        jornadaService.cerrarJornadasPendientes();

    }
    @PostConstruct
public void verificar() {

    System.out.println(
        "***** STARTUP VERIFIER EJECUTADO *****"
    );

    jornadaService.cerrarJornadasPendientes();
}
}