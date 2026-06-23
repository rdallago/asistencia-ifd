package com.instituto.asistencia.task;

import com.instituto.asistencia.service.JornadaService;
import java.time.LocalDateTime;
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

    @Scheduled(
            cron = "0 5 0 * * *",
            zone = "America/Argentina/Buenos_Aires"
    )
    public void ejecutar() {
          System.out.println(
            "***** CIERRE AUTOMÁTICO EJECUTADO: "
            + LocalDateTime.now()
        );

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