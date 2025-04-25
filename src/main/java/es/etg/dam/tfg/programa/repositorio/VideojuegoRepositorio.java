package es.etg.dam.tfg.programa.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.etg.dam.tfg.programa.modelo.Videojuego;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface VideojuegoRepositorio extends JpaRepository<Videojuego, Integer> {

    boolean existsByNombreAndFechaLanzamiento(String nombre, LocalDate fechaLanzamiento);

    Optional<Videojuego> findByNombreAndFechaLanzamiento(String nombre, LocalDate fechaLanzamiento);

    List<Videojuego> findByNombreContainingIgnoreCase(String nombre);
}
