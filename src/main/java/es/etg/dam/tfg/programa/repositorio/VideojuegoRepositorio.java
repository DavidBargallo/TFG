package es.etg.dam.tfg.programa.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import es.etg.dam.tfg.programa.modelo.Videojuego;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;

@Repository
public interface VideojuegoRepositorio extends JpaRepository<Videojuego, Integer> {

    boolean existsByNombreAndFechaLanzamiento(String nombre, LocalDate fechaLanzamiento);

    Optional<Videojuego> findByNombreAndFechaLanzamiento(String nombre, LocalDate fechaLanzamiento);

    @Query("SELECT v FROM Videojuego v LEFT JOIN FETCH v.generos LEFT JOIN FETCH v.consolas LEFT JOIN FETCH v.compania WHERE v.nombre = :nombre AND v.fechaLanzamiento = :fecha")
    Optional<Videojuego> findConRelacionesByNombreAndFecha(@Param("nombre") String nombre,
            @Param("fecha") LocalDate fecha);

    List<Videojuego> findByNombreContainingIgnoreCase(String nombre);

    Optional<Videojuego> findByNombre(String nombre);

    @EntityGraph(attributePaths = { "consolas", "generos" })
    Optional<Videojuego> findById(Integer id);

    @EntityGraph(attributePaths = { "consolas", "generos" })
    List<Videojuego> findAll();
}