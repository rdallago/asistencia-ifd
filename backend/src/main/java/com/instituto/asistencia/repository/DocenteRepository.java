package com.instituto.asistencia.repository;
import java.util.List;
import com.instituto.asistencia.entity.Docente;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
public interface DocenteRepository extends JpaRepository<Docente, Long> {

    Optional<Docente> findByDni(String dni);

    boolean existsByDni(String dni);
    List<Docente> findByActivoTrue();
  
    
}