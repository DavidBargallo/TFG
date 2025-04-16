package es.etg.dam.tfg.app.repositorio;

import es.etg.dam.tfg.app.modelo.Consola;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface ConsolaRepositorio extends JpaRepository<Consola, Integer> {

    boolean existsByNombreAndFechaLanzamiento(String nombre, LocalDate fechaLanzamiento);

    Optional<Consola> findByNombreAndFechaLanzamiento(String nombre, LocalDate fechaLanzamiento);
}
