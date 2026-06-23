package com.instituto.asistencia.repository;

import java.util.List;
import com.instituto.asistencia.entity.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository
        extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);
    
    boolean existsByUsername(
            String username);

    boolean existsByEmail(
            String email);

    List<Usuario> findByActivoTrue();

    List<Usuario> findByActivoFalse();
    
    boolean existsByDocenteId(
            Long docenteId);

    List<Usuario> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCaseOrUsernameContainingIgnoreCase(
            String nombre,
            String apellido,
            String username
    );

    


}