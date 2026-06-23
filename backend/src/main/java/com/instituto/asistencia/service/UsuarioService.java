package com.instituto.asistencia.service;

import com.instituto.asistencia.dto.UsuarioRequest;
import com.instituto.asistencia.entity.Docente;
import com.instituto.asistencia.entity.Usuario;
import com.instituto.asistencia.entity.enums.Rol;
import com.instituto.asistencia.repository.DocenteRepository;
import com.instituto.asistencia.repository.UsuarioRepository;
import com.instituto.asistencia.dto.UsuarioResponse;
import java.util.List;
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

    public UsuarioResponse crear(
        UsuarioRequest request){

        if (usuarioRepo.existsByUsername(
                request.getUsername())) {

            throw new RuntimeException(
                    "Ya existe un usuario con ese nombre");
        }

        if (usuarioRepo.existsByEmail(
                request.getEmail())) {

            throw new RuntimeException(
                    "Ya existe un usuario con ese correo");
        }

        if (request.getDocenteId() != null
                && usuarioRepo.existsByDocenteId(
                        request.getDocenteId())) {

            throw new RuntimeException(
                    "El docente ya tiene un usuario asociado");
        }

        Usuario usuario = new Usuario();

        usuario.setUsername(
                request.getUsername());

        usuario.setPassword(
                passwordEncoder.encode(
                        request.getPassword()));

        usuario.setNombre(
                request.getNombre());

        usuario.setApellido(
                request.getApellido());

        usuario.setEmail(
                request.getEmail());

        usuario.setRol(
                Rol.valueOf(
                        request.getRol()));

        usuario.setActivo(true);

        if (request.getDocenteId() != null) {

            Docente docente = docenteRepo
                    .findById(request.getDocenteId())
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Docente no encontrado"));

            usuario.setDocente(docente);
        }

        return convertirAResponse(
        usuarioRepo.save(usuario));
    }
    
    public List<UsuarioResponse> listar() {

    return usuarioRepo.findAll()
            .stream()
            .map(this::convertirAResponse)
            .toList();
}
    
    public UsuarioResponse buscarPorId(
        Long id) {

    Usuario usuario = usuarioRepo.findById(id)
            .orElseThrow(() ->
                    new RuntimeException(
                            "Usuario no encontrado"));

    return convertirAResponse(usuario);
}
    
    public List<UsuarioResponse> buscar(
        String texto) {

    return usuarioRepo.findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCaseOrUsernameContainingIgnoreCase(
                    texto,
                    texto,
                    texto
            )
            .stream()
            .map(this::convertirAResponse)
            .toList();
}
    
    public UsuarioResponse actualizar(
        Long id,
        UsuarioRequest request) {

    Usuario usuario = usuarioRepo.findById(id)
            .orElseThrow(() ->
                    new RuntimeException(
                            "Usuario no encontrado"));

    usuario.setNombre(
            request.getNombre());

    usuario.setApellido(
            request.getApellido());

    usuario.setEmail(
            request.getEmail());

    usuario.setRol(
            Rol.valueOf(
                    request.getRol()));

    if (request.getPassword() != null
            && !request.getPassword().isBlank()) {

        usuario.setPassword(
                passwordEncoder.encode(
                        request.getPassword()));
    }

    if (request.getDocenteId() != null) {

        if (usuario.getDocente() == null
                || !usuario.getDocente().getId()
                        .equals(request.getDocenteId())) {

            if (usuarioRepo.existsByDocenteId(
                    request.getDocenteId())) {

                throw new RuntimeException(
                        "El docente ya tiene un usuario asociado");
            }
        }

        Docente docente = docenteRepo.findById(
                request.getDocenteId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Docente no encontrado"));

        usuario.setDocente(docente);

    } else {

        usuario.setDocente(null);
    }

    return convertirAResponse(
            usuarioRepo.save(usuario));
}
    
    public void desactivar(
        Long id) {

    Usuario usuario = usuarioRepo.findById(id)
            .orElseThrow(() ->
                    new RuntimeException(
                            "Usuario no encontrado"));

    usuario.setActivo(false);

    usuarioRepo.save(usuario);
}
    
    
    private UsuarioResponse convertirAResponse(
        Usuario usuario) {

    UsuarioResponse response =
            new UsuarioResponse();

    response.setId(usuario.getId());

    response.setUsername(
            usuario.getUsername());

    response.setNombre(
            usuario.getNombre());

    response.setApellido(
            usuario.getApellido());

    response.setEmail(
            usuario.getEmail());

    response.setRol(
            usuario.getRol().name());

    response.setActivo(
            usuario.getActivo());

    if (usuario.getDocente() != null) {

        response.setDocenteId(
                usuario.getDocente().getId());

        response.setDocenteNombre(
                usuario.getDocente().getApellido()
                + ", "
                + usuario.getDocente().getNombre());
    }

    return response;
}
    public void activar(Long id) {

    Usuario usuario = usuarioRepo.findById(id)
            .orElseThrow(() ->
                    new RuntimeException(
                            "Usuario no encontrado"));

    usuario.setActivo(true);

    usuarioRepo.save(usuario);
}
    
}