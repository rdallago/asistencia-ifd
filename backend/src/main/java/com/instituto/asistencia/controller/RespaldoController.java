    package com.instituto.asistencia.controller;

    import com.instituto.asistencia.service.RespaldoService;
    import java.util.Map;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;
    import org.springframework.web.multipart.MultipartFile;

    import org.springframework.security.core.Authentication;
    import org.springframework.security.core.context.SecurityContextHolder;

    @RestController
    @RequestMapping("/api/respaldos")
    public class RespaldoController {

        private final RespaldoService respaldoService;

        public RespaldoController(
                RespaldoService respaldoService) {

            this.respaldoService = respaldoService;
        }

        @GetMapping("/prueba")
        public ResponseEntity<String> prueba() {

            return ResponseEntity.ok(
                    "Gestión de respaldos disponible"
            );
        }

        @PostMapping("/generar")
    public ResponseEntity<org.springframework.core.io.Resource> generar() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String usuario =
                authentication.getName();

        java.nio.file.Path archivo =
                respaldoService.generarBackup(usuario);

        org.springframework.core.io.Resource resource =
                new org.springframework.core.io.FileSystemResource(
                        archivo.toFile()
                );

        return ResponseEntity.ok()
                .header(
                        org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" +
                                archivo.getFileName().toString() +
                                "\""
                )
                .header(
                        org.springframework.http.HttpHeaders.CONTENT_TYPE,
                        "application/sql"
                )
                .body(resource);
    }

        @PostMapping("/restaurar")
        public ResponseEntity<Map<String, String>> restaurar(
                @RequestParam("archivo") MultipartFile archivo) {

         Authentication authentication =
            SecurityContextHolder
                    .getContext()
                    .getAuthentication();

    String usuario =
            authentication.getName();

    respaldoService.restaurarBackup(
            archivo,
            usuario
    );

   return ResponseEntity.ok(
        Map.of(
            "mensaje",
            "Respaldo restaurado correctamente."
        )
);
    }

    }