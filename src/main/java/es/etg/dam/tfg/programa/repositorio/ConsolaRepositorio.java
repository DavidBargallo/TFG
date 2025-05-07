package es.etg.dam.tfg.programa.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import es.etg.dam.tfg.programa.modelo.Consola;
import java.util.Optional;

@Repository
public interface ConsolaRepositorio extends JpaRepository<Consola, Integer> {
    Optional<Consola> findByNombre(String nombre);
    boolean existsByNombreIgnoreCase(String nombre);
}

