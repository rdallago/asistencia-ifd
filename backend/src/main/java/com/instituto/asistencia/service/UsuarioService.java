package com.instituto.asistencia.service;
import com.instituto.asistencia.repository.DocenteRepository;
import com.instituto.asistencia.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepo;
    private final DocenteRepository docenteRepo;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepo,
            DocenteRepository docenteRepo,
            PasswordEncoder passwordEncoder) {

        this.usuarioRepo = usuarioRepo;
        this.docenteRepo = docenteRepo;
        this.passwordEncoder = passwordEncoder;
    }

}