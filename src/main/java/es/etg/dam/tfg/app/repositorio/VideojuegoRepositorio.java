package es.etg.dam.tfg.app.repositorio;

import es.etg.dam.tfg.app.modelo.Videojuego;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface VideojuegoRepositorio extends JpaRepository<Videojuego, Integer> {

    boolean existsByNombreAndFechaLanzamiento(String nombre, LocalDate fechaLanzamiento);

    Optional<Videojuego> findByNombreAndFechaLanzamiento(String nombre, LocalDate fechaLanzamiento);

    List<Videojuego> findByNombreContainingIgnoreCase(String nombre);
}
