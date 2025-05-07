package es.etg.dam.tfg.programa.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import es.etg.dam.tfg.programa.modelo.Genero;
import java.util.Optional;

@Repository
public interface GeneroRepositorio extends JpaRepository<Genero, Integer> {
    Optional<Genero> findByNombre(String nombre);
}