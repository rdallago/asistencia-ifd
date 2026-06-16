package com.instituto.asistencia.controller;
import com.instituto.asistencia.dto.LoginRequest;
import com.instituto.asistencia.dto.LoginResponse;
import com.instituto.asistencia.entity.Usuario;
import com.instituto.asistencia.repository.UsuarioRepository;
import com.instituto.asistencia.security.JwtService;
import com.instituto.asistencia.dto.ChangePasswordRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }
    
    

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request) {

        Usuario usuario = usuarioRepository
                .findByUsername(request.getUsername())
                .orElse(null);

        if (usuario == null) {
            return ResponseEntity
                    .badRequest()
                    .body("Usuario inexistente");
        }

        if (!usuario.getActivo()) {
            return ResponseEntity
                    .badRequest()
                    .body("Usuario inactivo");
        }

        if (!passwordEncoder.matches(
                request.getPassword(),
                usuario.getPassword())) {

            return ResponseEntity
                    .badRequest()
                    .body("Contraseña incorrecta");
        }

        String token =
                jwtService.generateToken(
                        usuario.getUsername());

        return ResponseEntity.ok(
                new LoginResponse(
                        token,
                        usuario.getUsername(),
                        usuario.getRol().name()
                )
        );
    }
 @PutMapping("/cambiar-password")
public ResponseEntity<?> cambiarPassword(
        @RequestBody ChangePasswordRequest request) {
    
    Usuario usuario = usuarioRepository
            .findByUsername(request.getUsername())
            .orElse(null);

    if (usuario == null) {
        return ResponseEntity
                .badRequest()
                .body("Usuario inexistente");
    }

    if (!passwordEncoder.matches(
            request.getPasswordActual(),
            usuario.getPassword())) {

        return ResponseEntity
                .badRequest()
                .body("La contraseña actual es incorrecta");
    }

    usuario.setPassword(
            passwordEncoder.encode(
                    request.getPasswordNueva()));

    usuarioRepository.save(usuario);

    return ResponseEntity.ok(
             Map.of(
        "mensaje",
        "Contraseña actualizada correctamente"
    ));
}
 
}