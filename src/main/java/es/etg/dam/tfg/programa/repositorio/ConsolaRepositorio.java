package es.etg.dam.tfg.programa.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.etg.dam.tfg.programa.modelo.Consola;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConsolaRepositorio extends JpaRepository<Consola, Integer> {

    boolean existsByNombreAndFechaLanzamiento(String nombre, LocalDate fechaLanzamiento);

    Optional<Consola> findByNombreAndFechaLanzamiento(String nombre, LocalDate fechaLanzamiento);
    
    boolean existsByNombreIgnoreCase(String nombre);

    List<Consola> findByNombreContainingIgnoreCase(String nombre);
}
